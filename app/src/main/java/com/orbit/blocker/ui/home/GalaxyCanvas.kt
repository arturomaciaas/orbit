package com.orbit.blocker.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import com.orbit.blocker.data.model.CompletedPlanet
import com.orbit.blocker.data.model.GalaxyProgress
import com.orbit.blocker.data.model.PlanetStage
import com.orbit.blocker.data.model.PlanetType
import com.orbit.blocker.ui.theme.CometCyan
import com.orbit.blocker.ui.theme.MeteorRed
import com.orbit.blocker.ui.theme.NebulaViolet
import com.orbit.blocker.ui.theme.SolarGold
import com.orbit.blocker.ui.theme.SolarOrange
import com.orbit.blocker.ui.theme.StarWhite
import kotlin.math.cos
import kotlin.math.sin

/**
 * A shared, self-driving animation clock. Every canvas below runs off a single
 * [withFrameNanos] loop so scenes feel alive without callers managing time.
 */
@Composable
private fun rememberSceneClock(): androidx.compose.runtime.State<Float> {
    val time = remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        var last = 0L
        while (true) {
            withFrameNanos { now ->
                if (last != 0L) time.floatValue += (now - last) / 1_000_000_000f
                last = now
            }
        }
    }
    return time
}

// ---------------------------------------------------------------------------------------
// PLANET VIEW — close-up of the single active planet being grown.
// ---------------------------------------------------------------------------------------

/**
 * Close-up of the active planet at its current lifecycle [stage]. Moons appear from the
 * MOON stage and rings (for ringed types) at the RINGS stage. [meteorProgress] draws a
 * meteor streak + impact flash when a strike fires.
 */
@Composable
fun PlanetCanvas(
    type: PlanetType,
    stage: PlanetStage,
    modifier: Modifier = Modifier,
    meteorProgress: Float = 0f,
) {
    val time by rememberSceneClock()
    val visual = remember(type) { PlanetVisual.forType(type) }

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val unit = size.minDimension
        // Scale the close-up so even small planet types fill the frame nicely.
        val displayRadius = unit * 0.30f
        val moons = visual.visibleMoons(stage)
        val rings = visual.showRings(stage)

        drawPlanetAtmosphere(center, displayRadius, visual, time)
        if (rings) drawPlanetRing(center, unit, visual, time, front = false)
        drawPlanetBody(center, displayRadius, visual, time)
        if (rings) drawPlanetRing(center, unit, visual, time, front = true)
        drawPlanetMoons(center, displayRadius, moons, visual, time)
        if (meteorProgress > 0f) drawMeteor(meteorProgress)
    }
}

// ---------------------------------------------------------------------------------------
// SOLAR SYSTEM VIEW — the central star anchor with completed planets in their orbit slots.
// ---------------------------------------------------------------------------------------

/**
 * The current solar system: a central star (always present) with the [planets] completed
 * so far, each placed in its own orbit slot. Empty slots are hinted with a faint orbit
 * ring so the "progress toward 8" reads at a glance.
 *
 * When [doomedPlanetId] names one of [planets] and [impactProgress] > 0, a large meteor
 * flies in and strikes that planet: the planet shakes as the meteor approaches, then
 * explodes into debris at impact. The doomed planet's orbital angle is frozen for the
 * duration so the meteor has a stable target.
 */
@Composable
fun SolarSystemCanvas(
    planets: List<CompletedPlanet>,
    modifier: Modifier = Modifier,
    doomedPlanetId: Long? = null,
    impactProgress: Float = 0f,
) {
    val time by rememberSceneClock()
    // Freeze the doomed planet's angle when the impact sequence begins, so the meteor aims true.
    val frozenAngle = remember { mutableFloatStateOf(0f) }
    val frozen = remember { androidx.compose.runtime.mutableStateOf(false) }
    if (impactProgress <= 0f) frozen.value = false

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val unit = size.minDimension
        val slots = GalaxyProgress.PLANETS_PER_SYSTEM

        drawStar(center, unit * 0.10f, time)

        val bySlot = planets.associateBy { it.slot }
        var doomedPos: Offset? = null
        var doomedVisual: PlanetVisual? = null

        for (slot in 0 until slots) {
            val orbitR = unit * (0.20f + 0.030f * slot)
            drawCircle(
                color = StarWhite.copy(alpha = 0.06f),
                radius = orbitR,
                center = center,
                style = Stroke(width = 1.2f),
            )
            val planet = bySlot[slot] ?: continue
            val speed = 0.5f - slot * 0.03f
            val isDoomed = planet.id == doomedPlanetId && impactProgress > 0f

            val angle = if (isDoomed) {
                if (!frozen.value) {
                    frozenAngle.floatValue = time * speed + slot * (6.2831855f / slots)
                    frozen.value = true
                }
                frozenAngle.floatValue
            } else {
                time * speed + slot * (6.2831855f / slots)
            }
            val pos = orbitPoint(center, orbitR, angle)
            val visual = PlanetVisual.forType(planet.type)

            if (isDoomed) {
                doomedPos = pos
                doomedVisual = visual
                // Draw the doomed planet with a pre-impact shake + reddening as the meteor nears.
                drawDoomedPlanet(pos, unit, visual, time, impactProgress)
            } else {
                drawSystemPlanet(pos, unit, visual, time)
            }
        }

        // The targeted meteor + explosion, aimed at the doomed planet.
        if (doomedPos != null && impactProgress > 0f) {
            drawTargetedMeteor(doomedPos, impactProgress)
        }
    }
}

// ---------------------------------------------------------------------------------------
// GALAXY VIEW — each completed solar system is a star; a growing spiral of them.
// ---------------------------------------------------------------------------------------

/**
 * The galaxy: [systemsCompleted] completed solar systems, each rendered as a star arranged
 * along faint spiral arms around a bright core. A dense starfield backdrop plus drifting
 * spiral haze convey scale. Empty until the first full system is finished.
 */
@Composable
fun GalaxyCanvas(
    systemsCompleted: Int,
    modifier: Modifier = Modifier,
) {
    val time by rememberSceneClock()

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val unit = size.minDimension

        drawSpiralArms(center, unit, time)
        // Bright galactic core.
        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color.White, SolarGold.copy(alpha = 0.8f), Color.Transparent),
                center = center,
                radius = unit * 0.14f,
            ),
            radius = unit * 0.14f,
            center = center,
        )

        // Each completed system is a star placed along a spiral, spinning slowly outward.
        val rotation = time * 0.05f
        for (i in 0 until systemsCompleted) {
            val t = (i + 1) / (systemsCompleted + 1f)
            val r = unit * (0.10f + t * 0.36f)
            val angle = i * 2.399963f + rotation // golden-angle scatter
            val pos = Offset(center.x + cos(angle) * r, center.y + sin(angle) * r * 0.7f)
            val twinkle = 0.6f + 0.4f * sin(time * 2f + i)
            drawCircle(SolarGold.copy(alpha = 0.35f * twinkle), unit * 0.03f, pos)
            drawCircle(StarWhite.copy(alpha = twinkle), unit * 0.014f, pos)
        }
    }
}

// region planet body + atmosphere
private fun DrawScope.drawPlanetBody(center: Offset, radius: Float, visual: PlanetVisual, time: Float) {
    val spin = time * 0.25f
    // Erratic worlds wobble their disc slightly.
    val wobble = if (visual.erratic) 1f + 0.05f * sin(time * 3.3f) else 1f
    val r = radius * wobble

    drawCircle(
        brush = Brush.radialGradient(
            colors = visual.surface,
            center = Offset(center.x - r * 0.35f, center.y - r * 0.35f),
            radius = r * 1.5f,
        ),
        radius = r,
        center = center,
    )

    // Rotating surface detail, clipped to the disc.
    val discPath = Path().apply {
        addOval(Rect(center.x - r, center.y - r, center.x + r, center.y + r))
    }
    clipPath(discPath) {
        val bandColor = visual.accent.copy(alpha = 0.35f)
        val bands = if (visual.erratic) 7 else 5
        for (i in 0 until bands) {
            val phase = spin + i * 1.3f
            val cx = center.x + sin(phase) * r * 0.8f
            val bandY = center.y - r * 0.6f + i * r * (1.2f / bands)
            drawCircle(bandColor, r * (0.30f - i * 0.03f).coerceAtLeast(0.05f), Offset(cx, bandY))
        }
        // A storm spot orbiting the surface (brighter/erratic on rogue worlds).
        val sx = center.x + sin(spin * 1.4f) * r * 0.6f
        val sy = center.y + cos(spin * 1.4f) * r * 0.3f
        drawCircle(visual.accent.copy(alpha = if (visual.erratic) 0.7f else 0.5f), r * 0.16f, Offset(sx, sy))
    }

    // Terminator shadow for a 3D lit look.
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f)),
            center = Offset(center.x + r * 0.45f, center.y + r * 0.45f),
            radius = r * 1.4f,
        ),
        radius = r,
        center = center,
    )
    // Rim light on the lit edge.
    drawCircle(
        color = StarWhite.copy(alpha = 0.5f),
        radius = r,
        center = center,
        style = Stroke(width = r * 0.03f),
    )
}

private fun DrawScope.drawPlanetAtmosphere(center: Offset, radius: Float, visual: PlanetVisual, time: Float) {
    val pulse = 1f + 0.06f * sin(time * 1.5f)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(visual.accent.copy(alpha = 0.35f), Color.Transparent),
            center = center,
            radius = radius * 1.9f * pulse,
        ),
        radius = radius * 1.9f * pulse,
        center = center,
    )
}
// endregion

// region planet rings + moons
private fun DrawScope.drawPlanetRing(center: Offset, unit: Float, visual: PlanetVisual, time: Float, front: Boolean) {
    if (!visual.hasRings) return
    // Scale the type's ring geometry up for the close-up view.
    val rx = unit * (visual.ringRx + 0.06f)
    val ry = unit * (visual.ringRy + 0.02f)
    val wobbleAmt = if (visual.erratic) 10f else 4f
    val tilt = visual.ringTilt + wobbleAmt * sin(time * 0.3f)
    rotate(degrees = tilt, pivot = center) {
        val topLeft = Offset(center.x - rx, center.y - ry)
        val ringSize = Size(rx * 2, ry * 2)
        drawArc(
            brush = Brush.horizontalGradient(visual.ringColors),
            startAngle = if (front) 0f else 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = topLeft,
            size = ringSize,
            style = Stroke(width = unit * 0.02f),
        )
    }
}

private fun DrawScope.drawPlanetMoons(center: Offset, radius: Float, count: Int, visual: PlanetVisual, time: Float) {
    if (count <= 0) return
    for (i in 0 until count) {
        val orbitR = radius * (1.7f + 0.5f * i)
        val speed = 0.9f - i * 0.15f
        val angle = time * speed + i * (6.2831855f / count)
        // Motion trail.
        for (t in 1..5) {
            val a = angle - t * 0.07f
            val pos = orbitPoint(center, orbitR, a)
            drawCircle(StarWhite.copy(alpha = 0.10f * (5 - t) / 5f), radius * 0.10f, pos)
        }
        val pos = orbitPoint(center, orbitR, angle)
        // Dwarf's "moon" is a tiny captured asteroid; others get a proper moon.
        val moonR = if (visual.type == PlanetType.DWARF) radius * 0.08f else radius * 0.14f
        drawCircle(StarWhite.copy(alpha = 0.35f), moonR * 1.8f, pos)
        drawCircle(StarWhite, moonR, pos)
    }
}
// endregion

// region star + system planet
private fun DrawScope.drawStar(center: Offset, radius: Float, time: Float) {
    val pulse = 1f + 0.05f * sin(time * 2f)
    // Corona glow.
    drawCircle(
        brush = Brush.radialGradient(
            listOf(SolarGold.copy(alpha = 0.45f), Color.Transparent),
            center = center,
            radius = radius * 3f * pulse,
        ),
        radius = radius * 3f * pulse,
        center = center,
    )
    // Hot core.
    drawCircle(
        brush = Brush.radialGradient(
            listOf(Color.White, SolarGold, SolarOrange.copy(alpha = 0.9f)),
            center = center,
            radius = radius * pulse,
        ),
        radius = radius * pulse,
        center = center,
    )
}

/** A small rendering of a completed planet orbiting in the solar-system view. */
private fun DrawScope.drawSystemPlanet(pos: Offset, unit: Float, visual: PlanetVisual, time: Float) {
    val r = unit * (0.020f + visual.radiusFraction * 0.12f)
    // Glow.
    drawCircle(visual.accent.copy(alpha = 0.30f), r * 1.9f, pos)
    // Body.
    drawCircle(
        brush = Brush.radialGradient(
            colors = visual.surface,
            center = Offset(pos.x - r * 0.35f, pos.y - r * 0.35f),
            radius = r * 1.5f,
        ),
        radius = r,
        center = pos,
    )
    // A hint of a ring for ringed types.
    if (visual.hasRings) {
        rotate(degrees = visual.ringTilt, pivot = pos) {
            drawArc(
                brush = Brush.horizontalGradient(visual.ringColors),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(pos.x - r * 1.8f, pos.y - r * 0.6f),
                size = Size(r * 3.6f, r * 1.2f),
                style = Stroke(width = r * 0.35f),
            )
        }
    }
}

private fun orbitPoint(center: Offset, r: Float, angle: Float): Offset =
    Offset(center.x + cos(angle) * r, center.y + sin(angle) * r * 0.55f)
// endregion

// region galaxy spiral
private fun DrawScope.drawSpiralArms(center: Offset, unit: Float, time: Float) {
    val arms = 2
    val pointsPerArm = 60
    val maxR = unit * 0.48f
    val rotation = time * 0.15f
    for (arm in 0 until arms) {
        val armOffset = arm * (Math.PI.toFloat())
        for (p in 0 until pointsPerArm) {
            val t = p / pointsPerArm.toFloat()
            val r = t * maxR
            val angle = t * 6f + armOffset + rotation
            val pos = Offset(center.x + cos(angle) * r, center.y + sin(angle) * r * 0.6f)
            val alpha = (1f - t) * 0.25f
            drawCircle(NebulaViolet.copy(alpha = alpha), unit * 0.01f * (1f - t) + 1.5f, pos)
        }
    }
}
// endregion

// region meteor
/**
 * The impact timeline (all driven by [progress] 0f..1f) is split into two phases:
 *  - Approach (0.0 .. [IMPACT_T]): the meteor streaks in from off-screen toward [target].
 *  - Blast ([IMPACT_T] .. 1.0): a bright flash, an expanding shockwave ring, and debris
 *    thrown outward from the impact point.
 */
/** Fraction of the meteor timeline at which the meteor reaches its target and the blast begins. */
const val IMPACT_FRACTION = 0.62f

private const val IMPACT_T = IMPACT_FRACTION

/**
 * A big, dramatic meteor aimed at [target] (the doomed planet's position), followed by an
 * explosion. Used in the solar-system view where there is a planet to destroy.
 */
private fun DrawScope.drawTargetedMeteor(target: Offset, progress: Float) {
    val unit = size.minDimension
    // Come in from the upper-left, off the canvas, toward the target.
    val entry = Offset(target.x - unit * 0.9f, target.y - unit * 1.1f)

    if (progress < IMPACT_T) {
        val t = progress / IMPACT_T
        val head = lerpOffset(entry, target, t)
        val tail = lerpOffset(entry, target, (t - 0.22f).coerceAtLeast(0f))
        // Thick glowing trail.
        drawLine(
            brush = Brush.linearGradient(listOf(Color.Transparent, SolarOrange, MeteorRed), start = tail, end = head),
            start = tail, end = head, strokeWidth = unit * 0.03f,
        )
        // Flaming head with a hot core.
        drawCircle(MeteorRed.copy(alpha = 0.5f), unit * 0.05f, head)
        drawCircle(SolarOrange, unit * 0.03f, head)
        drawCircle(SolarGold, unit * 0.016f, head)
    } else {
        val b = (progress - IMPACT_T) / (1f - IMPACT_T) // 0..1 blast progress
        drawExplosion(target, unit, b)
    }
}

/** A short, dramatic setback meteor for the planet close-up view (no planet to destroy). */
private fun DrawScope.drawMeteor(progress: Float) {
    val unit = size.minDimension
    val target = Offset(size.width / 2f, size.height / 2f)
    val entry = Offset(size.width * -0.1f, size.height * -0.15f)
    if (progress < IMPACT_T) {
        val t = progress / IMPACT_T
        val head = lerpOffset(entry, target, t)
        val tail = lerpOffset(entry, target, (t - 0.22f).coerceAtLeast(0f))
        drawLine(
            brush = Brush.linearGradient(listOf(Color.Transparent, SolarOrange, MeteorRed), start = tail, end = head),
            start = tail, end = head, strokeWidth = unit * 0.03f,
        )
        drawCircle(SolarOrange, unit * 0.028f, head)
        drawCircle(SolarGold, unit * 0.014f, head)
    } else {
        val b = (progress - IMPACT_T) / (1f - IMPACT_T)
        drawExplosion(target, unit, b)
    }
}

/** Flash + expanding shockwave ring + flying debris centered at [at]. */
private fun DrawScope.drawExplosion(at: Offset, unit: Float, b: Float) {
    // Central flash, fading out.
    val flashAlpha = (1f - b).coerceIn(0f, 1f)
    drawCircle(
        brush = Brush.radialGradient(
            listOf(Color.White.copy(alpha = flashAlpha), SolarGold.copy(alpha = 0.7f * flashAlpha), Color.Transparent),
            center = at,
            radius = unit * (0.10f + 0.20f * b),
        ),
        radius = unit * (0.10f + 0.20f * b),
        center = at,
    )
    // Expanding shockwave ring.
    val ringR = unit * (0.04f + 0.34f * b)
    drawCircle(
        color = MeteorRed.copy(alpha = 0.6f * (1f - b)),
        radius = ringR,
        center = at,
        style = Stroke(width = unit * 0.012f * (1f - b).coerceAtLeast(0.2f)),
    )
    // Debris thrown radially outward.
    val debris = 10
    for (i in 0 until debris) {
        val ang = i * (6.2831855f / debris) + i * 0.3f
        val dist = unit * (0.05f + 0.30f * b)
        val p = Offset(at.x + cos(ang) * dist, at.y + sin(ang) * dist * 0.85f)
        val r = unit * 0.012f * (1f - b).coerceAtLeast(0f)
        if (r > 0f) drawCircle(SolarOrange.copy(alpha = 1f - b), r, p)
    }
}

/**
 * Draws the doomed planet during the approach: it reddens and shakes harder as the meteor
 * closes in, then vanishes at impact (its debris comes from [drawExplosion]).
 */
private fun DrawScope.drawDoomedPlanet(pos: Offset, unit: Float, visual: PlanetVisual, time: Float, progress: Float) {
    if (progress >= IMPACT_T) return // gone — the explosion takes over
    val approach = progress / IMPACT_T
    // Shake amplitude grows as impact nears.
    val shake = unit * 0.012f * approach
    val shaken = Offset(
        pos.x + sin(time * 40f) * shake,
        pos.y + cos(time * 37f) * shake,
    )
    drawSystemPlanet(shaken, unit, visual, time)
    // Reddening warning glow.
    val r = unit * (0.020f + visual.radiusFraction * 0.12f)
    drawCircle(MeteorRed.copy(alpha = 0.5f * approach), r * 2.2f, shaken)
}

private fun lerpOffset(a: Offset, b: Offset, t: Float): Offset =
    Offset(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t)
// endregion

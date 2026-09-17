package com.orbit.blocker.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.orbit.blocker.ui.theme.CometCyan
import com.orbit.blocker.ui.theme.MeteorRed
import com.orbit.blocker.ui.theme.NebulaBlue
import com.orbit.blocker.ui.theme.NebulaPink
import com.orbit.blocker.ui.theme.NebulaViolet
import com.orbit.blocker.ui.theme.SolarGold
import com.orbit.blocker.ui.theme.SolarOrange
import com.orbit.blocker.ui.theme.StarWhite
import kotlin.math.cos
import kotlin.math.sin

/**
 * Rich, animated centerpiece for the Home screen. Everything is driven by a single
 * [withFrameNanos] time loop so the scene feels alive:
 *  - a shaded planet (or glowing star) with a rotating surface and terminator shadow,
 *  - a soft glowing atmosphere halo that pulses,
 *  - animated gradient rings,
 *  - moons/planets orbiting on elliptical paths with fading motion trails,
 *  - spiral arms for the GALAXY stage.
 *
 * [meteorProgress] (0f..1f) draws a meteor streak + impact flash when a setback fires.
 */
@Composable
fun GalaxyCanvas(
    visual: GalaxyVisual,
    modifier: Modifier = Modifier,
    meteorProgress: Float = 0f,
) {
    var time by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        var last = 0L
        while (true) {
            withFrameNanos { now ->
                if (last != 0L) time += (now - last) / 1_000_000_000f
                last = now
            }
        }
    }

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val unit = size.minDimension

        drawAtmosphereGlow(center, unit, visual, time)
        if (visual.hasSpiralHaze) drawSpiralArms(center, unit, time)
        drawRingsBehind(center, unit, visual, time)
        drawCentralBody(center, unit, visual, time)
        drawRingsFront(center, unit, visual, time)
        drawOrbiters(center, unit, visual, time)
        if (meteorProgress > 0f) drawMeteor(meteorProgress)
    }
}

// region central body
private fun DrawScope.drawCentralBody(center: Offset, unit: Float, visual: GalaxyVisual, time: Float) {
    val radius = unit * (if (visual.hasCentralStar) 0.15f else 0.19f)
    val spin = time * 0.25f

    if (visual.hasCentralStar) {
        // Glowing star: hot core -> orange -> gold corona, gently pulsing.
        val pulse = 1f + 0.04f * sin(time * 2f)
        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color.White, SolarGold, SolarOrange.copy(alpha = 0.9f)),
                center = center,
                radius = radius * pulse,
            ),
            radius = radius * pulse,
            center = center,
        )
        return
    }

    // Planet base sphere with a cool day-side gradient.
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(CometCyan, NebulaBlue, NebulaViolet),
            center = Offset(center.x - radius * 0.35f, center.y - radius * 0.35f),
            radius = radius * 1.5f,
        ),
        radius = radius,
        center = center,
    )

    // Rotating surface bands/spots, clipped to the planet disc, to convey spin.
    val discPath = Path().apply {
        addOval(Rect(center.x - radius, center.y - radius, center.x + radius, center.y + radius))
    }
    clipPath(discPath) {
        val bandColor = NebulaViolet.copy(alpha = 0.35f)
        for (i in 0 until 5) {
            val phase = spin + i * 1.3f
            val cx = center.x + sin(phase) * radius * 0.8f
            val bandY = center.y - radius * 0.6f + i * radius * 0.32f
            drawCircle(
                color = bandColor,
                radius = radius * (0.30f - i * 0.03f),
                center = Offset(cx, bandY),
            )
        }
        // A brighter storm spot orbiting the surface.
        val sx = center.x + sin(spin * 1.4f) * radius * 0.6f
        val sy = center.y + cos(spin * 1.4f) * radius * 0.3f
        drawCircle(NebulaPink.copy(alpha = 0.5f), radius * 0.16f, Offset(sx, sy))
    }

    // Terminator: shadow on the far side for a 3D lit look.
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f)),
            center = Offset(center.x + radius * 0.45f, center.y + radius * 0.45f),
            radius = radius * 1.4f,
        ),
        radius = radius,
        center = center,
    )
    // Rim light on the lit edge.
    drawCircle(
        color = StarWhite.copy(alpha = 0.5f),
        radius = radius,
        center = center,
        style = Stroke(width = unit * 0.006f),
    )
}

private fun DrawScope.drawAtmosphereGlow(center: Offset, unit: Float, visual: GalaxyVisual, time: Float) {
    val baseR = unit * (if (visual.hasCentralStar) 0.15f else 0.19f)
    val pulse = 1f + 0.06f * sin(time * 1.5f)
    val glowColor = if (visual.hasCentralStar) SolarGold else CometCyan
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(glowColor.copy(alpha = 0.35f), Color.Transparent),
            center = center,
            radius = baseR * 2.4f * pulse,
        ),
        radius = baseR * 2.4f * pulse,
        center = center,
    )
}
// endregion

// region rings
private fun DrawScope.drawRingsBehind(center: Offset, unit: Float, visual: GalaxyVisual, time: Float) {
    if (!visual.hasRings) return
    drawRing(center, unit, time, front = false)
}

private fun DrawScope.drawRingsFront(center: Offset, unit: Float, visual: GalaxyVisual, time: Float) {
    if (!visual.hasRings) return
    drawRing(center, unit, time, front = true)
}

/**
 * Draws an elliptical ring split into a back half (behind the planet) and front half,
 * so the planet appears to sit within the ring. [front] selects which arc to render.
 */
private fun DrawScope.drawRing(center: Offset, unit: Float, time: Float, front: Boolean) {
    val rx = unit * 0.34f
    val ry = unit * 0.12f
    val tilt = 18f + 4f * sin(time * 0.3f) // subtle wobble
    rotate(degrees = tilt, pivot = center) {
        val topLeft = Offset(center.x - rx, center.y - ry)
        val ringSize = Size(rx * 2, ry * 2)
        val sweep = 180f
        val start = if (front) 0f else 180f
        drawArc(
            brush = Brush.horizontalGradient(
                listOf(CometCyan.copy(alpha = 0.7f), NebulaViolet.copy(alpha = 0.7f)),
            ),
            startAngle = start,
            sweepAngle = sweep,
            useCenter = false,
            topLeft = topLeft,
            size = ringSize,
            style = Stroke(width = unit * 0.02f),
        )
    }
}
// endregion

// region orbiters
private fun DrawScope.drawOrbiters(center: Offset, unit: Float, visual: GalaxyVisual, time: Float) {
    val n = visual.orbitingBodies
    if (n <= 0) return
    val colors = listOf(NebulaPink, CometCyan, SolarGold, NebulaViolet, NebulaBlue)
    for (i in 0 until n) {
        val orbitR = unit * (0.30f + 0.055f * i)
        val speed = 0.6f - i * 0.05f
        val angle = time * speed + i * (6.28f / n)
        // Slight ellipse for depth.
        val bodyColor = colors[i % colors.size]

        // Motion trail: a few faded ghosts behind the body.
        for (t in 1..6) {
            val a = angle - t * 0.06f
            val pos = orbitPoint(center, orbitR, a)
            drawCircle(bodyColor.copy(alpha = 0.10f * (6 - t) / 6f), unit * 0.018f, pos)
        }
        val pos = orbitPoint(center, orbitR, angle)
        // Faint orbit path.
        drawCircle(
            color = StarWhite.copy(alpha = 0.05f),
            radius = orbitR,
            center = center,
            style = Stroke(width = 1.2f),
        )
        // Glow + body.
        drawCircle(bodyColor.copy(alpha = 0.35f), unit * 0.045f, pos)
        drawCircle(bodyColor, unit * 0.024f, pos)
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
private fun DrawScope.drawMeteor(progress: Float) {
    val start = Offset(size.width * 0.02f, size.height * 0.0f)
    val end = Offset(size.width * 0.5f, size.height * 0.5f)
    val head = Offset(
        x = start.x + (end.x - start.x) * progress,
        y = start.y + (end.y - start.y) * progress,
    )
    val tailT = (progress - 0.18f).coerceAtLeast(0f)
    val tail = Offset(
        x = start.x + (end.x - start.x) * tailT,
        y = start.y + (end.y - start.y) * tailT,
    )
    drawLine(
        brush = Brush.linearGradient(
            listOf(Color.Transparent, SolarOrange, MeteorRed),
            start = tail,
            end = head,
        ),
        start = tail,
        end = head,
        strokeWidth = 8f,
    )
    drawCircle(SolarGold, 6f, head)
    if (progress > 0.82f) {
        val flash = (1f - progress) / 0.18f
        drawCircle(
            brush = Brush.radialGradient(
                listOf(MeteorRed.copy(alpha = 0.6f * flash), Color.Transparent),
                center = Offset(size.width / 2f, size.height / 2f),
                radius = size.minDimension * 0.28f,
            ),
            radius = size.minDimension * 0.28f,
            center = Offset(size.width / 2f, size.height / 2f),
        )
    }
}
// endregion



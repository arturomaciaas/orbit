package com.orbit.blocker.ui.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.orbit.blocker.ui.theme.CometCyan
import com.orbit.blocker.ui.theme.MeteorRed
import com.orbit.blocker.ui.theme.NebulaBlue
import com.orbit.blocker.ui.theme.NebulaViolet
import com.orbit.blocker.ui.theme.SolarGold
import com.orbit.blocker.ui.theme.StarWhite
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

/**
 * Animated space visualization. Renders a starfield plus the cosmos described by
 * [visual]; a slow [orbitAngle] rotates the orbiting bodies. When [meteorProgress]
 * is > 0 (0f..1f), a meteor streaks across and flashes the central body.
 */
@Composable
fun GalaxyCanvas(
    visual: GalaxyVisual,
    modifier: Modifier = Modifier,
    meteorProgress: Float = 0f,
) {
    val transition = rememberInfiniteTransition(label = "galaxy")
    val orbitAngle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 24_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "orbit",
    )
    val twinkle by transition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2_400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "twinkle",
    )

    // Deterministic starfield so it doesn't reshuffle on every recomposition.
    val stars = remember(visual.starDensity) {
        val rnd = Random(visual.starDensity * 31L)
        List(visual.starDensity) {
            StarSpec(x = rnd.nextFloat(), y = rnd.nextFloat(), r = rnd.nextFloat())
        }
    }

    Canvas(modifier = modifier) {
        drawStarfield(stars, twinkle)
        drawCosmos(visual, orbitAngle)
        if (meteorProgress > 0f) drawMeteor(meteorProgress)
    }
}

private data class StarSpec(val x: Float, val y: Float, val r: Float)

private fun DrawScope.drawStarfield(stars: List<StarSpec>, twinkle: Float) {
    stars.forEach { s ->
        val radius = 0.5f + s.r * 1.8f
        drawCircle(
            color = StarWhite.copy(alpha = 0.25f + s.r * 0.5f * twinkle),
            radius = radius,
            center = Offset(s.x * size.width, s.y * size.height),
        )
    }
}

private fun DrawScope.drawCosmos(visual: GalaxyVisual, orbitAngle: Float) {
    val center = Offset(size.width / 2f, size.height / 2f)
    val unit = min(size.width, size.height)

    if (visual.hasSpiralHaze) {
        drawCircle(
            color = NebulaViolet.copy(alpha = 0.12f),
            radius = unit * 0.45f,
            center = center,
        )
        drawCircle(
            color = NebulaBlue.copy(alpha = 0.10f),
            radius = unit * 0.32f,
            center = center,
        )
    }

    // Central body: a star (system/galaxy) or a planet.
    val centralRadius = unit * (if (visual.hasCentralStar) 0.09f else 0.14f)
    val centralColor = if (visual.hasCentralStar) SolarGold else NebulaBlue
    drawCircle(color = centralColor, radius = centralRadius, center = center)
    if (visual.hasCentralStar) {
        drawCircle(color = SolarGold.copy(alpha = 0.25f), radius = centralRadius * 1.9f, center = center)
    }

    // Rings around the central body.
    if (visual.hasRings) {
        drawCircle(
            color = CometCyan.copy(alpha = 0.6f),
            radius = centralRadius * 1.7f,
            center = center,
            style = Stroke(width = unit * 0.008f),
        )
    }

    // Orbiting bodies spaced around evenly, each on its own orbit radius.
    val n = visual.orbitingBodies
    for (i in 0 until n) {
        val orbitR = unit * (0.20f + 0.045f * i)
        val angleDeg = orbitAngle + (360f / n.coerceAtLeast(1)) * i
        val rad = Math.toRadians(angleDeg.toDouble())
        val pos = Offset(
            x = center.x + (orbitR * cos(rad)).toFloat(),
            y = center.y + (orbitR * sin(rad)).toFloat(),
        )
        // Faint orbit path.
        drawCircle(
            color = StarWhite.copy(alpha = 0.06f),
            radius = orbitR,
            center = center,
            style = Stroke(width = 1.2f),
        )
        val bodyColor = if (i % 2 == 0) NebulaViolet else CometCyan
        drawCircle(color = bodyColor, radius = unit * 0.022f, center = pos)
    }
}

private fun DrawScope.drawMeteor(progress: Float) {
    // Streak from top-left to center, fading a red trail.
    val start = Offset(size.width * 0.05f, size.height * 0.05f)
    val end = Offset(size.width * 0.5f, size.height * 0.5f)
    val head = Offset(
        x = start.x + (end.x - start.x) * progress,
        y = start.y + (end.y - start.y) * progress,
    )
    val tail = Offset(
        x = start.x + (end.x - start.x) * (progress - 0.15f).coerceAtLeast(0f),
        y = start.y + (end.y - start.y) * (progress - 0.15f).coerceAtLeast(0f),
    )
    drawLine(
        color = MeteorRed.copy(alpha = 0.9f),
        start = tail,
        end = head,
        strokeWidth = 6f,
    )
    drawCircle(color = SolarGold, radius = 5f, center = head)
    // Flash near the center when the meteor lands.
    if (progress > 0.85f) {
        drawCircle(
            color = MeteorRed.copy(alpha = (1f - progress) / 0.15f * 0.5f),
            radius = min(size.width, size.height) * 0.2f,
            center = Offset(size.width / 2f, size.height / 2f),
        )
    }
}

package com.orbit.blocker.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.orbit.blocker.ui.theme.CometCyan
import com.orbit.blocker.ui.theme.NebulaBlue
import com.orbit.blocker.ui.theme.NebulaPink
import com.orbit.blocker.ui.theme.NebulaViolet
import com.orbit.blocker.ui.theme.SpaceBackground
import com.orbit.blocker.ui.theme.SpaceBackgroundDeep
import com.orbit.blocker.ui.theme.StarWhite
import kotlin.math.sin
import kotlin.random.Random

/**
 * A living, animated deep-space backdrop meant to sit behind every screen.
 *
 * Layers, back to front:
 *  1. Vertical space gradient + soft drifting nebula blobs.
 *  2. Three parallax star layers (far/mid/near) that drift slowly upward and wrap,
 *     each star twinkling on its own phase.
 *  3. Periodic shooting stars that streak across and fade.
 *
 * Driven by a single [withFrameNanos] loop for smooth ~60fps motion, independent of
 * recomposition.
 */
@Composable
fun SpaceBackground(
    modifier: Modifier = Modifier,
    starCount: Int = 140,
) {
    // Elapsed time in seconds, advanced every frame.
    var timeSec by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        var last = 0L
        while (true) {
            withFrameNanos { now ->
                if (last != 0L) timeSec += (now - last) / 1_000_000_000f
                last = now
            }
        }
    }

    val stars = remember(starCount) { generateStars(starCount) }
    val shootingStars = remember { generateShootingStars() }

    Canvas(modifier = modifier.fillMaxSize()) {
        drawBackdrop()
        drawNebula(timeSec)
        drawStars(stars, timeSec)
        drawShootingStars(shootingStars, timeSec)
    }
}

private data class Star(
    val x: Float,      // 0..1 of width
    val y: Float,      // 0..1 of height
    val radius: Float, // px baseline
    val layer: Int,    // 0 far .. 2 near (parallax speed)
    val twinklePhase: Float,
    val twinkleSpeed: Float,
    val tint: Color,
)

private data class ShootingStar(
    val startX: Float,
    val startY: Float,
    val angle: Float,     // radians
    val length: Float,    // fraction of min dimension
    val period: Float,    // seconds between appearances
    val offset: Float,     // phase offset in seconds
    val speed: Float,      // fraction of travel per second
)

private fun generateStars(count: Int): List<Star> {
    val rnd = Random(1234)
    val tints = listOf(StarWhite, NebulaBlue, CometCyan, NebulaViolet)
    return List(count) {
        val layer = rnd.nextInt(3)
        Star(
            x = rnd.nextFloat(),
            y = rnd.nextFloat(),
            radius = (0.6f + rnd.nextFloat() * (1f + layer)) ,
            layer = layer,
            twinklePhase = rnd.nextFloat() * (2f * Math.PI.toFloat()),
            twinkleSpeed = 0.6f + rnd.nextFloat() * 2.2f,
            tint = tints[rnd.nextInt(tints.size)],
        )
    }
}

private fun generateShootingStars(): List<ShootingStar> {
    val rnd = Random(99)
    return List(3) { i ->
        ShootingStar(
            startX = 0.05f + rnd.nextFloat() * 0.5f,
            startY = 0.05f + rnd.nextFloat() * 0.4f,
            angle = (0.6f + rnd.nextFloat() * 0.5f), // down-right
            length = 0.18f + rnd.nextFloat() * 0.12f,
            period = 6f + rnd.nextFloat() * 6f,
            offset = i * 3.5f + rnd.nextFloat() * 2f,
            speed = 0.9f + rnd.nextFloat() * 0.5f,
        )
    }
}

private fun DrawScope.drawBackdrop() {
    drawRect(
        brush = Brush.verticalGradient(
            listOf(SpaceBackground, SpaceBackgroundDeep),
        ),
    )
}

private fun DrawScope.drawNebula(time: Float) {
    // A few slow-drifting translucent color clouds.
    val blobs = listOf(
        Triple(NebulaViolet, 0.25f, 0.22f),
        Triple(NebulaBlue, 0.75f, 0.35f),
        Triple(NebulaPink, 0.55f, 0.8f),
    )
    blobs.forEachIndexed { i, (color, baseX, baseY) ->
        val driftX = sin(time * 0.05f + i) * 0.03f
        val driftY = sin(time * 0.04f + i * 1.7f) * 0.03f
        val center = Offset((baseX + driftX) * size.width, (baseY + driftY) * size.height)
        val radius = size.minDimension * (0.35f + 0.05f * sin(time * 0.06f + i))
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(color.copy(alpha = 0.10f), Color.Transparent),
                center = center,
                radius = radius,
            ),
            radius = radius,
            center = center,
        )
    }
}

private fun DrawScope.drawStars(stars: List<Star>, time: Float) {
    // Parallax: near layers drift faster. Motion wraps within 0..1.
    val layerSpeed = floatArrayOf(0.006f, 0.012f, 0.022f)
    stars.forEach { s ->
        val drift = (time * layerSpeed[s.layer]) % 1f
        val y = ((s.y - drift) % 1f + 1f) % 1f
        val twinkle = 0.5f + 0.5f * sin(time * s.twinkleSpeed + s.twinklePhase)
        val alpha = (0.25f + 0.6f * twinkle) * (0.5f + 0.25f * s.layer)
        val center = Offset(s.x * size.width, y * size.height)
        // Glow for the brighter near stars.
        if (s.layer == 2) {
            drawCircle(
                color = s.tint.copy(alpha = alpha * 0.25f),
                radius = s.radius * 3f,
                center = center,
            )
        }
        drawCircle(color = s.tint.copy(alpha = alpha), radius = s.radius, center = center)
    }
}

private fun DrawScope.drawShootingStars(shooting: List<ShootingStar>, time: Float) {
    shooting.forEach { s ->
        val local = ((time + s.offset) % s.period)
        val travel = local * s.speed
        // Only visible during the first ~1s of each period.
        if (travel > 1f) return@forEach
        val progress = travel
        val dx = kotlin.math.cos(s.angle)
        val dy = kotlin.math.sin(s.angle)
        val span = s.length * size.minDimension
        val headX = (s.startX * size.width) + dx * progress * size.width
        val headY = (s.startY * size.height) + dy * progress * size.height
        val tailX = headX - dx * span
        val tailY = headY - dy * span
        val fade = (1f - progress).coerceIn(0f, 1f)
        drawLine(
            brush = Brush.linearGradient(
                colors = listOf(Color.Transparent, StarWhite.copy(alpha = 0.9f * fade)),
                start = Offset(tailX, tailY),
                end = Offset(headX, headY),
            ),
            start = Offset(tailX, tailY),
            end = Offset(headX, headY),
            strokeWidth = 3f,
        )
        drawCircle(color = StarWhite.copy(alpha = fade), radius = 2.5f, center = Offset(headX, headY))
    }
}

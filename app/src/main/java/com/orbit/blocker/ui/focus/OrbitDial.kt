package com.orbit.blocker.ui.focus

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbit.blocker.ui.theme.CometCyan
import com.orbit.blocker.ui.theme.GlassBorder
import com.orbit.blocker.ui.theme.GlassFill
import com.orbit.blocker.ui.theme.NebulaViolet
import com.orbit.blocker.ui.theme.StarWhite
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/** Dial configuration: 5 minutes to 3 hours, in 5-minute steps. */
object DialRange {
    const val MIN_MINUTES = 5
    const val MAX_MINUTES = 180
    const val STEP_MINUTES = 5

    val steps: Int = (MAX_MINUTES - MIN_MINUTES) / STEP_MINUTES // number of intervals

    /** Maps a minutes value to a 0f..1f fraction of the dial sweep. */
    fun fractionOf(minutes: Int): Float =
        ((minutes - MIN_MINUTES).toFloat() / (MAX_MINUTES - MIN_MINUTES)).coerceIn(0f, 1f)

    /** Maps a 0f..1f fraction to a snapped minutes value. */
    fun minutesOf(fraction: Float): Int {
        val raw = MIN_MINUTES + fraction.coerceIn(0f, 1f) * (MAX_MINUTES - MIN_MINUTES)
        val snapped = (raw / STEP_MINUTES).roundToInt() * STEP_MINUTES
        return snapped.coerceIn(MIN_MINUTES, MAX_MINUTES)
    }
}

/** Formats a minutes count as a big center label, e.g. "25", "1:30". */
private fun formatDialMinutes(minutes: Int): Pair<String, String> {
    return if (minutes < 60) {
        minutes.toString() to "min"
    } else {
        val h = minutes / 60
        val m = minutes % 60
        val label = if (m == 0) "${h}h" else "${h}h ${m}m"
        label to "focus"
    }
}

/**
 * A liquid-glass rotating dial for picking a focus duration. The user drags a glowing
 * knob around the ring; the arc from the top fills to the knob, and the center shows the
 * selected time. Range and snapping come from [DialRange].
 *
 * The sweep starts at the top (12 o'clock) and runs clockwise, leaving a small gap at the
 * bottom so the min and max ends are visually distinct.
 */
@Composable
fun OrbitDial(
    minutes: Int,
    onMinutesChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    diameter: Dp = 260.dp,
    enabled: Boolean = true,
) {
    val textMeasurer = rememberTextMeasurer()

    // The dial occupies a 300-degree sweep with a 60-degree gap at the bottom.
    val sweepDegrees = 300f
    val startAngleDeg = 120f // canvas 0deg is at 3 o'clock; 120 puts start at lower-left

    Box(modifier = modifier.size(diameter), contentAlignment = Alignment.Center) {
        Canvas(
            modifier = Modifier
                .size(diameter)
                .then(
                    if (enabled) {
                        Modifier.pointerInput(Unit) {
                            detectDragGestures { change, _ ->
                                change.consume()
                                val minsFromTouch = angleToMinutes(
                                    touch = change.position,
                                    size = size.toComposeSize(),
                                    startAngleDeg = startAngleDeg,
                                    sweepDegrees = sweepDegrees,
                                )
                                onMinutesChange(minsFromTouch)
                            }
                        }.pointerInput(Unit) {
                            detectTapGestures { pos ->
                                val minsFromTouch = angleToMinutes(
                                    touch = pos,
                                    size = size.toComposeSize(),
                                    startAngleDeg = startAngleDeg,
                                    sweepDegrees = sweepDegrees,
                                )
                                onMinutesChange(minsFromTouch)
                            }
                        }
                    } else Modifier,
                ),
        ) {
            drawDial(
                fraction = DialRange.fractionOf(minutes),
                startAngleDeg = startAngleDeg,
                sweepDegrees = sweepDegrees,
                showKnob = enabled,
            )
            drawCenterLabel(textMeasurer, minutes)
        }
    }
}

/**
 * A read-only variant used during an active session: the arc represents elapsed progress
 * and the center shows a caller-supplied label (the countdown).
 */
@Composable
fun OrbitCountdownDial(
    progress: Float,
    centerLabel: String,
    centerSub: String,
    modifier: Modifier = Modifier,
    diameter: Dp = 300.dp,
) {
    val textMeasurer = rememberTextMeasurer()
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

    val sweepDegrees = 300f
    val startAngleDeg = 120f

    Box(modifier = modifier.size(diameter), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(diameter)) {
            drawDial(
                fraction = progress.coerceIn(0f, 1f),
                startAngleDeg = startAngleDeg,
                sweepDegrees = sweepDegrees,
                showKnob = false,
                pulse = time,
            )
            drawCountdownLabel(textMeasurer, centerLabel, centerSub)
        }
    }
}

private fun IntSize.toComposeSize(): Size =
    Size(width.toFloat(), height.toFloat())

/** Converts a touch point into a snapped minutes value along the dial sweep. */
private fun angleToMinutes(
    touch: Offset,
    size: Size,
    startAngleDeg: Float,
    sweepDegrees: Float,
): Int {
    val center = Offset(size.width / 2f, size.height / 2f)
    val dx = touch.x - center.x
    val dy = touch.y - center.y
    // atan2 with canvas coordinates: 0deg at 3 o'clock, positive clockwise.
    var deg = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
    if (deg < 0f) deg += 360f
    // Offset so the sweep starts at startAngleDeg and runs clockwise.
    var rel = deg - startAngleDeg
    if (rel < 0f) rel += 360f
    // Points inside the bottom gap snap to the nearest end.
    if (rel > sweepDegrees) {
        return if (rel - sweepDegrees < (360f - sweepDegrees) / 2f) DialRange.MAX_MINUTES
        else DialRange.MIN_MINUTES
    }
    val fraction = rel / sweepDegrees
    return DialRange.minutesOf(fraction)
}

// region drawing
private fun DrawScope.drawDial(
    fraction: Float,
    startAngleDeg: Float,
    sweepDegrees: Float,
    showKnob: Boolean,
    pulse: Float = 0f,
) {
    val center = Offset(size.width / 2f, size.height / 2f)
    val stroke = size.minDimension * 0.06f
    val radius = (size.minDimension - stroke) / 2f * 0.92f
    val topLeft = Offset(center.x - radius, center.y - radius)
    val arcSize = Size(radius * 2, radius * 2)

    // Track (unfilled portion) — subtle glass hairline.
    drawArc(
        color = GlassFill.copy(alpha = 0.16f),
        startAngle = startAngleDeg,
        sweepAngle = sweepDegrees,
        useCenter = false,
        topLeft = topLeft,
        size = arcSize,
        style = Stroke(width = stroke, cap = StrokeCap.Round),
    )

    // Tick marks around the track.
    val majorEvery = 3 // a tick every 15 minutes (3 * 5min steps)
    for (i in 0..DialRange.steps) {
        val f = i.toFloat() / DialRange.steps
        val ang = Math.toRadians((startAngleDeg + sweepDegrees * f).toDouble())
        val isMajor = i % majorEvery == 0
        val inner = radius - stroke * (if (isMajor) 0.95f else 0.55f)
        val outer = radius + stroke * 0.15f
        val c = if (f <= fraction) CometCyan else StarWhite.copy(alpha = 0.22f)
        drawLine(
            color = c.copy(alpha = if (isMajor) (c.alpha) else c.alpha * 0.7f),
            start = Offset(center.x + cos(ang).toFloat() * inner, center.y + sin(ang).toFloat() * inner),
            end = Offset(center.x + cos(ang).toFloat() * outer, center.y + sin(ang).toFloat() * outer),
            strokeWidth = if (isMajor) 3f else 1.5f,
        )
    }

    // Filled progress arc with a glowing gradient.
    val filledSweep = sweepDegrees * fraction.coerceIn(0f, 1f)
    // Outer glow pass.
    drawArc(
        brush = Brush.sweepGradient(listOf(CometCyan, NebulaViolet, CometCyan), center = center),
        startAngle = startAngleDeg,
        sweepAngle = filledSweep,
        useCenter = false,
        topLeft = topLeft,
        size = arcSize,
        style = Stroke(width = stroke * 1.8f, cap = StrokeCap.Round),
        alpha = 0.22f,
    )
    drawArc(
        brush = Brush.sweepGradient(listOf(CometCyan, NebulaViolet, CometCyan), center = center),
        startAngle = startAngleDeg,
        sweepAngle = filledSweep,
        useCenter = false,
        topLeft = topLeft,
        size = arcSize,
        style = Stroke(width = stroke, cap = StrokeCap.Round),
    )

    // Inner atmosphere glow disc.
    val discRadius = radius - stroke * 1.2f
    drawCircle(
        brush = Brush.radialGradient(
            listOf(
                NebulaViolet.copy(alpha = 0.16f + 0.04f * sin(pulse * 1.5f)),
                CometCyan.copy(alpha = 0.05f),
                Color.Transparent,
            ),
            center = center,
            radius = discRadius,
        ),
        radius = discRadius,
        center = center,
    )

    // Knob at the end of the filled arc.
    if (showKnob) {
        val knobAngle = Math.toRadians((startAngleDeg + filledSweep).toDouble())
        val knobPos = Offset(
            center.x + cos(knobAngle).toFloat() * radius,
            center.y + sin(knobAngle).toFloat() * radius,
        )
        drawCircle(CometCyan.copy(alpha = 0.30f), stroke * 1.1f, knobPos)
        drawCircle(StarWhite, stroke * 0.55f, knobPos)
        drawCircle(CometCyan, stroke * 0.34f, knobPos)
    }
}

private fun DrawScope.drawCenterLabel(measurer: TextMeasurer, minutes: Int) {
    val (big, small) = formatDialMinutes(minutes)
    drawCenteredText(measurer, big, small)
}

private fun DrawScope.drawCountdownLabel(measurer: TextMeasurer, big: String, small: String) {
    drawCenteredText(measurer, big, small)
}

private fun DrawScope.drawCenteredText(measurer: TextMeasurer, big: String, small: String) {
    val center = Offset(size.width / 2f, size.height / 2f)
    // Convert px sizes to sp using the current density so text scales with the dial.
    val bigSp = (size.minDimension * 0.18f / density).sp
    val smallSp = (size.minDimension * 0.045f / density).sp
    val bigLayout = measurer.measure(
        text = big,
        style = TextStyle(color = StarWhite, fontSize = bigSp, fontWeight = FontWeight.Bold),
    )
    val smallLayout = measurer.measure(
        text = small.uppercase(),
        style = TextStyle(
            color = GlassBorder.copy(alpha = 0.7f),
            fontSize = smallSp,
            letterSpacing = 3.sp,
        ),
    )
    val totalH = bigLayout.size.height + smallLayout.size.height + 4f
    val topY = center.y - totalH / 2f
    drawText(
        textLayoutResult = bigLayout,
        topLeft = Offset(center.x - bigLayout.size.width / 2f, topY),
    )
    drawText(
        textLayoutResult = smallLayout,
        topLeft = Offset(center.x - smallLayout.size.width / 2f, topY + bigLayout.size.height + 4f),
    )
}
// endregion

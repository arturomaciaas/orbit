package com.orbit.blocker.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbit.blocker.ui.theme.CometCyan
import com.orbit.blocker.ui.theme.GlassBorder
import com.orbit.blocker.ui.theme.GlassFill
import com.orbit.blocker.ui.theme.GlassHighlight
import com.orbit.blocker.ui.theme.NebulaViolet

/**
 * Frosted "liquid glass" container: a translucent tinted fill, a soft top-to-bottom
 * sheen, and a hairline border with rounded corners. Sits over the animated space
 * background so the backdrop shows through.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    fillAlpha: Float = 0.10f,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(cornerRadius)
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        GlassHighlight.copy(alpha = fillAlpha + 0.06f),
                        GlassFill.copy(alpha = fillAlpha),
                        GlassFill.copy(alpha = fillAlpha * 0.6f),
                    ),
                ),
                shape = shape,
            )
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        listOf(
                            GlassBorder.copy(alpha = 0.35f),
                            GlassBorder.copy(alpha = 0.08f),
                        ),
                    ),
                ),
                shape = shape,
            ),
    ) {
        Column(modifier = Modifier.padding(contentPadding), content = content)
    }
}

/**
 * A glass "pill" used for chips/toggles. Selected state gets an accent gradient fill
 * and glow; unselected stays subtle glass. Animates between the two.
 */
@Composable
fun GlassPill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(50)
    val fill by animateFloatAsState(if (selected) 1f else 0f, tween(220), label = "pillFill")
    val contentColor by animateColorAsState(
        if (selected) Color.Black else MaterialTheme.colorScheme.onSurface,
        tween(220),
        label = "pillText",
    )
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                brush = Brush.horizontalGradient(
                    listOf(
                        lerpAlpha(GlassFill, CometCyan, fill, base = 0.10f, top = 0.9f),
                        lerpAlpha(GlassFill, NebulaViolet, fill, base = 0.08f, top = 0.9f),
                    ),
                ),
                shape = shape,
            )
            .border(BorderStroke(1.dp, GlassBorder.copy(alpha = 0.25f + 0.25f * fill)), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 9.dp),
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge, color = contentColor)
    }
}

/** A thin animated glass progress bar with a glowing gradient fill. */
@Composable
fun GlassProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    barHeight: Dp = 10.dp,
) {
    val animated by animateFloatAsState(progress.coerceIn(0f, 1f), tween(600), label = "progress")
    val shape = RoundedCornerShape(50)
    Box(
        modifier = modifier
            .height(barHeight)
            .clip(shape)
            .background(GlassFill.copy(alpha = 0.12f), shape)
            .border(BorderStroke(1.dp, GlassBorder.copy(alpha = 0.2f)), shape),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animated)
                .height(barHeight)
                .clip(shape)
                .background(
                    Brush.horizontalGradient(listOf(CometCyan, NebulaViolet)),
                    shape,
                ),
        )
    }
}

/** Section label used above content groups. */
@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 2.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier,
    )
}

private fun lerpAlpha(from: Color, to: Color, t: Float, base: Float, top: Float): Color {
    val c = Color(
        red = from.red + (to.red - from.red) * t,
        green = from.green + (to.green - from.green) * t,
        blue = from.blue + (to.blue - from.blue) * t,
    )
    return c.copy(alpha = base + (top - base) * t)
}

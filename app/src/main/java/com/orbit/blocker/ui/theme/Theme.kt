package com.orbit.blocker.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Orbit is always dark (space aesthetic); we don't offer a light scheme.
private val OrbitColorScheme = darkColorScheme(
    primary = NebulaBlue,
    onPrimary = SpaceBackground,
    secondary = NebulaViolet,
    onSecondary = SpaceBackground,
    tertiary = CometCyan,
    background = SpaceBackground,
    onBackground = StarWhite,
    surface = SpaceSurface,
    onSurface = StarWhite,
    surfaceVariant = SpaceSurfaceVariant,
    onSurfaceVariant = MutedText,
    error = MeteorRed,
    onError = SpaceBackground,
)

@Composable
fun OrbitTheme(
    // Kept for API symmetry; Orbit ignores the system setting and stays dark.
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = OrbitColorScheme,
        typography = OrbitTypography,
        content = content,
    )
}

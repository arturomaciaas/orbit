package com.orbit.blocker.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.ui.graphics.vector.ImageVector

/** Top-level destinations reachable from the bottom navigation bar. */
enum class OrbitDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
    /** Whether this destination appears in the bottom dock. */
    val showInBottomBar: Boolean = true,
) {
    HOME("home", "Cosmos", Icons.Filled.Public),
    FOCUS("focus", "Focus", Icons.Filled.Timer),
    // Quiz Bank is reachable from Settings, so it is hidden from the dock.
    QUIZ("quiz", "Quiz Bank", Icons.Filled.Quiz, showInBottomBar = false),
    // Developer preview is reachable from Settings, hidden from the dock.
    DEVELOPER("developer", "Developer", Icons.Filled.Quiz, showInBottomBar = false),
    DIGEST("digest", "Digest", Icons.Filled.Notifications),
    SETTINGS("settings", "Settings", Icons.Filled.Settings),
    ;

    companion object {
        val START = HOME

        /** Destinations shown in the bottom dock, in order. */
        val bottomBar: List<OrbitDestination> = entries.filter { it.showInBottomBar }

        fun fromRoute(route: String?): OrbitDestination =
            entries.firstOrNull { it.route == route } ?: START
    }
}

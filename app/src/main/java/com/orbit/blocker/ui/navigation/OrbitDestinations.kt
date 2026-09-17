package com.orbit.blocker.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
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
) {
    HOME("home", "Cosmos", Icons.Filled.Public),
    FOCUS("focus", "Focus", Icons.Filled.Timer),
    BLOCKS("blocks", "Blocks", Icons.Filled.Block),
    QUIZ("quiz", "Quiz Bank", Icons.Filled.Quiz),
    DIGEST("digest", "Digest", Icons.Filled.Notifications),
    SETTINGS("settings", "Settings", Icons.Filled.Settings),
    ;

    companion object {
        val START = HOME
        fun fromRoute(route: String?): OrbitDestination =
            entries.firstOrNull { it.route == route } ?: START
    }
}

package com.orbit.blocker.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.orbit.blocker.ui.blocks.BlocksScreen
import com.orbit.blocker.ui.components.DockItem
import com.orbit.blocker.ui.components.GlassDock
import com.orbit.blocker.ui.components.SpaceBackground
import com.orbit.blocker.ui.digest.DigestScreen
import com.orbit.blocker.ui.focus.FocusScreen
import com.orbit.blocker.ui.home.HomeScreen
import com.orbit.blocker.ui.navigation.OrbitDestination
import com.orbit.blocker.ui.quizbank.QuizBankScreen
import com.orbit.blocker.ui.settings.SettingsScreen

/**
 * Root composable. A single animated [SpaceBackground] lives behind a transparent
 * Scaffold so every screen shares the living backdrop; the bottom bar is translucent
 * glass. Screens render their own content over the shared background.
 */
@Composable
fun OrbitApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        SpaceBackground(modifier = Modifier.fillMaxSize())

        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                GlassDock(
                    items = OrbitDestination.bottomBar.map { dest ->
                        val selected = backStackEntry?.destination?.hierarchy?.any {
                            it.route == dest.route
                        } == true
                        DockItem(
                            icon = dest.icon,
                            label = dest.label,
                            selected = selected,
                            onClick = {
                                navController.navigate(dest.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                        )
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                )
            },
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = OrbitDestination.START.route,
                modifier = Modifier.padding(padding),
            ) {
                composable(OrbitDestination.HOME.route) { HomeScreen() }
                composable(OrbitDestination.FOCUS.route) { FocusScreen() }
                composable(OrbitDestination.BLOCKS.route) { BlocksScreen() }
                composable(OrbitDestination.QUIZ.route) { QuizBankScreen() }
                composable(OrbitDestination.DIGEST.route) { DigestScreen() }
                composable(OrbitDestination.SETTINGS.route) {
                    SettingsScreen(
                        onOpenQuizBank = { navController.navigate(OrbitDestination.QUIZ.route) },
                    )
                }
            }
        }
    }
}

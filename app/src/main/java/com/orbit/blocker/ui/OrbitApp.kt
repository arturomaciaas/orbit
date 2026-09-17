package com.orbit.blocker.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.orbit.blocker.ui.components.SpaceBackground
import com.orbit.blocker.ui.digest.DigestScreen
import com.orbit.blocker.ui.focus.FocusScreen
import com.orbit.blocker.ui.home.HomeScreen
import com.orbit.blocker.ui.navigation.OrbitDestination
import com.orbit.blocker.ui.quizbank.QuizBankScreen
import com.orbit.blocker.ui.quizgate.QuizGateScreen
import com.orbit.blocker.ui.settings.SettingsScreen
import com.orbit.blocker.ui.theme.CometCyan
import com.orbit.blocker.ui.theme.GlassFill

/** Non-tab route for exercising the quiz gate standalone. */
private const val ROUTE_QUIZ_GATE_DEMO = "quiz_gate_demo"

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
                NavigationBar(
                    containerColor = GlassFill.copy(alpha = 0.06f),
                    tonalElevation = 0.dp,
                ) {
                    OrbitDestination.entries.forEach { dest ->
                        val selected = backStackEntry?.destination?.hierarchy?.any {
                            it.route == dest.route
                        } == true
                        NavigationBarItem(
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
                            icon = { Icon(dest.icon, contentDescription = dest.label) },
                            label = { Text(dest.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.Black,
                                selectedTextColor = CometCyan,
                                indicatorColor = CometCyan,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                        )
                    }
                }
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
                        onOpenQuizGateDemo = { navController.navigate(ROUTE_QUIZ_GATE_DEMO) },
                    )
                }
                composable(ROUTE_QUIZ_GATE_DEMO) {
                    QuizGateScreen(
                        onPassed = { navController.popBackStack() },
                        onFailed = { navController.popBackStack() },
                        onDismissNotEnough = { navController.popBackStack() },
                    )
                }
            }
        }
    }
}

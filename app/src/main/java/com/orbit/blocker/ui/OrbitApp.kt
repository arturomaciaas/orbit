package com.orbit.blocker.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.orbit.blocker.ui.blocks.BlocksScreen
import com.orbit.blocker.ui.digest.DigestScreen
import com.orbit.blocker.ui.focus.FocusScreen
import com.orbit.blocker.ui.home.HomeScreen
import com.orbit.blocker.ui.navigation.OrbitDestination
import com.orbit.blocker.ui.quizbank.QuizBankScreen
import com.orbit.blocker.ui.quizgate.QuizGateScreen
import com.orbit.blocker.ui.settings.SettingsScreen

/** Non-tab route for exercising the quiz gate standalone (Task 3 demo). */
private const val ROUTE_QUIZ_GATE_DEMO = "quiz_gate_demo"

/**
 * Root composable: a bottom-nav Scaffold hosting the five top-level destinations,
 * plus off-tab routes such as the quiz-gate demo. Screens are filled in per task.
 */
@Composable
fun OrbitApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()

    Scaffold(
        bottomBar = {
            NavigationBar {
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
                        icon = { androidx.compose.material3.Icon(dest.icon, contentDescription = dest.label) },
                        label = { Text(dest.label) },
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
            composable(OrbitDestination.HOME.route) {
                HomeScreen()
            }
            composable(OrbitDestination.FOCUS.route) {
                FocusScreen()
            }
            composable(OrbitDestination.BLOCKS.route) {
                BlocksScreen()
            }
            composable(OrbitDestination.QUIZ.route) {
                QuizBankScreen()
            }
            composable(OrbitDestination.DIGEST.route) {
                DigestScreen()
            }
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

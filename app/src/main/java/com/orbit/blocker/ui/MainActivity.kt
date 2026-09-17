package com.orbit.blocker.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.orbit.blocker.ui.onboarding.OnboardingScreen
import com.orbit.blocker.ui.theme.OrbitTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            OrbitTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    OrbitRoot()
                }
            }
        }
    }
}

/** Routes between first-run onboarding and the main app. */
@Composable
private fun OrbitRoot(viewModel: RootViewModel = hiltViewModel()) {
    val onboardingComplete by viewModel.onboardingComplete.collectAsStateWithLifecycle()

    when (onboardingComplete) {
        null -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        false -> OnboardingScreen(onFinished = { /* flag flips reactively -> app shows */ })
        true -> OrbitApp()
    }
}

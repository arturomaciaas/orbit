package com.orbit.blocker.ui.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.orbit.blocker.domain.gamification.GalaxyEngine
import com.orbit.blocker.ui.components.GlassCard
import com.orbit.blocker.ui.components.GlassProgressBar
import com.orbit.blocker.ui.components.SectionLabel

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val progress by viewModel.progress.collectAsStateWithLifecycle()
    val visual = remember(progress.stage) { GalaxyVisual.forStage(progress.stage) }

    // Trigger a meteor animation whenever the strike count increases.
    val meteor = remember { Animatable(0f) }
    LaunchedEffect(progress.meteorStrikes) {
        if (progress.meteorStrikes > 0) {
            meteor.snapTo(0f)
            meteor.animateTo(1f, animationSpec = tween(900))
            meteor.snapTo(0f)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column {
            SectionLabel("Your cosmos")
            Text(
                visual.title,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        // The planet/galaxy sits directly on the space backdrop (no card) for maximum impact.
        Box(
            modifier = Modifier.fillMaxWidth().aspectRatio(1f),
            contentAlignment = Alignment.Center,
        ) {
            GalaxyCanvas(
                visual = visual,
                meteorProgress = meteor.value,
                modifier = Modifier.fillMaxSize(),
            )
        }

        // Growth card.
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Growth", style = MaterialTheme.typography.titleLarge)
                Text(
                    "${(GalaxyEngine.overallFraction(progress) * 100).toInt()}%",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Text(
                "Progress toward the next stage",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp),
            )
            GlassProgressBar(progress = progress.progress, modifier = Modifier.fillMaxWidth())
        }

        // Stats card.
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterHorizontally),
            ) {
                Stat(label = "Sessions", value = progress.totalSessionsCompleted.toString())
                Stat(label = "Streak", value = "${progress.currentStreakDays}d")
                Stat(label = "Best", value = "${progress.longestStreakDays}d")
            }
        }
    }
}

@Composable
private fun Stat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            label.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

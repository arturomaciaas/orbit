package com.orbit.blocker.ui.focus

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.orbit.blocker.domain.block.DurationFormatter
import com.orbit.blocker.domain.focus.FocusTimer
import com.orbit.blocker.service.FocusSessionService
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

private val DURATION_OPTIONS_MIN = listOf(15L, 25L, 45L, 60L, 90L)

@Composable
fun FocusScreen(viewModel: FocusViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    if (state.session.active) {
        ActiveSession(
            endsAt = state.session.endsAt ?: 0L,
            startedAt = state.session.startedAt ?: 0L,
            blockedCount = state.session.blockedPackages.size,
            onStop = { FocusSessionService.stop(context) },
        )
        return
    }

    // Setup state
    val selected = remember { mutableStateMapOf<String, Boolean>() }
    var durationMin by remember { mutableStateOf(25L) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Focus session", style = MaterialTheme.typography.headlineLarge)
        Text(
            "Pick apps to lock down and a duration. Opening them during the session triggers the quiz gate.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 8.dp),
        )

        Text("Duration", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 8.dp)) {
            DURATION_OPTIONS_MIN.forEach { m ->
                FilterChip(
                    selected = durationMin == m,
                    onClick = { durationMin = m },
                    label = { Text("${m}m") },
                )
            }
        }

        Text("Apps to lock", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 8.dp))
        if (state.managedApps.isEmpty()) {
            Text(
                "No managed apps yet. Add some in the Blocks tab first.",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        LazyColumn(modifier = Modifier.weight(1f).padding(top = 8.dp)) {
            items(state.managedApps, key = { it.packageName }) { app ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(
                        checked = selected[app.packageName] == true,
                        onCheckedChange = { selected[app.packageName] = it },
                    )
                    Text(app.appLabel, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        val chosen = selected.filterValues { it }.keys
        Button(
            onClick = {
                FocusSessionService.start(
                    context = context,
                    packages = chosen,
                    durationMillis = TimeUnit.MINUTES.toMillis(durationMin),
                )
            },
            enabled = chosen.isNotEmpty(),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        ) {
            Text("Start focus session")
        }
    }
}

@Composable
private fun ActiveSession(
    endsAt: Long,
    startedAt: Long,
    blockedCount: Int,
    onStop: () -> Unit,
) {
    // Local ticking clock for the countdown display.
    var now by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(endsAt) {
        while (true) {
            now = System.currentTimeMillis()
            if (FocusTimer.isComplete(endsAt, now)) break
            delay(1_000)
        }
    }

    val remaining = FocusTimer.remainingMillis(endsAt, now)

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Focusing", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
        Text(
            DurationFormatter.format(remaining),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(vertical = 12.dp),
        )
        Text(
            "$blockedCount app${if (blockedCount == 1) "" else "s"} locked",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        OutlinedButton(onClick = onStop, modifier = Modifier.padding(top = 24.dp)) {
            Text("End session early")
        }
    }
}

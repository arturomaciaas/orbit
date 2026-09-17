package com.orbit.blocker.ui.blocks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.orbit.blocker.data.apps.InstalledApp
import com.orbit.blocker.data.model.BlockMode
import com.orbit.blocker.data.model.NotificationTier
import com.orbit.blocker.domain.block.BlockRuleFactory
import com.orbit.blocker.domain.block.DurationFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlocksScreen(viewModel: BlocksViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var sheetApp by remember { mutableStateOf<InstalledApp?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Blocks", style = MaterialTheme.typography.headlineLarge)

        if (state.managedApps.isNotEmpty()) {
            Text(
                "Blocked apps",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
            )
        }

        OutlinedTextField(
            value = state.query,
            onValueChange = viewModel::onQueryChange,
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            placeholder = { Text("Search apps") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        )

        if (state.loading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) { CircularProgressIndicator() }
            return@Column
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            if (state.managedApps.isNotEmpty()) {
                items(state.managedApps, key = { "managed_${it.packageName}" }) { managed ->
                    ManagedAppRow(
                        managed = managed,
                        onRemoveRule = viewModel::removeRule,
                        onRemoveAll = { viewModel.removeManagedApp(managed.packageName) },
                        onSetTier = { tier -> viewModel.setNotificationTier(managed.packageName, tier) },
                    )
                }
                item {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    Text("All apps", style = MaterialTheme.typography.titleLarge)
                }
            }

            items(state.filteredInstalledApps, key = { "installed_${it.packageName}" }) { app ->
                InstalledAppRow(app = app, onClick = { sheetApp = app })
            }
        }
    }

    sheetApp?.let { app ->
        BlockAppSheet(
            app = app,
            onDismiss = { sheetApp = null },
            onConfirm = { choice ->
                when (choice) {
                    is BlockChoice.Focus -> viewModel.addFocusBlock(app)
                    is BlockChoice.Duration -> viewModel.addDurationBlock(app, choice.amount, choice.unit)
                }
                sheetApp = null
            },
        )
    }
}

@Composable
private fun ManagedAppRow(
    managed: ManagedAppUi,
    onRemoveRule: (Long) -> Unit,
    onRemoveAll: () -> Unit,
    onSetTier: (NotificationTier) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                managed.label,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(end = 8.dp),
            )
        }
        managed.rules.filter { it.enabled }.forEach { rule ->
            val description = when (rule.mode) {
                BlockMode.FOCUS_SESSION -> "Focus sessions"
                BlockMode.DURATION -> {
                    val remaining = BlockRuleFactory.remainingMillis(rule) ?: 0L
                    "Duration • ${DurationFormatter.format(remaining)} left"
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    description,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                TextButton(onClick = { onRemoveRule(rule.id) }) { Text("Remove") }
            }
        }

        // Notification tier selector.
        Text(
            "Notifications",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            NotificationTier.entries.forEach { tier ->
                FilterChip(
                    selected = managed.notificationTier == tier,
                    onClick = { onSetTier(tier) },
                    label = { Text(tier.chipLabel()) },
                )
            }
        }

        TextButton(onClick = onRemoveAll) { Text("Unmanage app") }
    }
}

private fun NotificationTier.chipLabel(): String = when (this) {
    NotificationTier.PRIORITY -> "Priority"
    NotificationTier.PEEK -> "Peek"
    NotificationTier.SUPPRESS -> "Suppress"
}

@Composable
private fun InstalledAppRow(app: InstalledApp, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(app.label, style = MaterialTheme.typography.bodyLarge)
        IconButton(onClick = onClick) {
            Icon(Icons.Filled.Block, contentDescription = "Block ${app.label}")
        }
    }
}

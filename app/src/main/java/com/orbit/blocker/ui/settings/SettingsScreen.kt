package com.orbit.blocker.ui.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.orbit.blocker.service.AccessibilityPermission
import com.orbit.blocker.service.NotificationAccessPermission

@Composable
fun SettingsScreen(
    onOpenQuizBank: () -> Unit,
    onOpenDeveloper: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val questionsRequired by viewModel.questionsRequired.collectAsStateWithLifecycle()
    val backupStatus by viewModel.backupStatus.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Storage Access Framework launchers for backup export/import.
    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json"),
    ) { uri -> uri?.let(viewModel::exportTo) }
    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri -> uri?.let(viewModel::importFrom) }

    // Re-check permission states whenever the screen resumes (user may have toggled them in system Settings).
    var accessibilityEnabled by remember { mutableStateOf(AccessibilityPermission.isEnabled(context)) }
    var notificationAccessEnabled by remember { mutableStateOf(NotificationAccessPermission.isEnabled(context)) }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                accessibilityEnabled = AccessibilityPermission.isEnabled(context)
                notificationAccessEnabled = NotificationAccessPermission.isEnabled(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineLarge)

        Column {
            Text("App blocking", style = MaterialTheme.typography.titleLarge)
            Text(
                if (accessibilityEnabled) "Orbit's accessibility service is on. Blocked apps will be gated."
                else "Turn on Orbit's accessibility service so it can gate blocked apps.",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            OutlinedButton(
                onClick = { AccessibilityPermission.openSettings(context) },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            ) {
                Text(if (accessibilityEnabled) "Accessibility settings" else "Enable accessibility service")
            }
        }

        Column {
            Text("Notification filtering", style = MaterialTheme.typography.titleLarge)
            Text(
                if (notificationAccessEnabled) "Notification access is on. Peek and suppressed apps are filtered."
                else "Grant notification access so Orbit can filter and collect notifications.",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            OutlinedButton(
                onClick = { NotificationAccessPermission.openSettings(context) },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            ) {
                Text(if (notificationAccessEnabled) "Notification access settings" else "Grant notification access")
            }
        }

        Column {
            Text("Questions required to unlock", style = MaterialTheme.typography.titleLarge)
            Text(
                "Answer this many correctly to earn a 5-minute access window.",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Slider(
                    value = questionsRequired.toFloat(),
                    onValueChange = { viewModel.setQuestionsRequired(it.toInt()) },
                    valueRange = 1f..10f,
                    steps = 8,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    "$questionsRequired",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 16.dp),
                )
            }
        }

        Column {
            Text("Quiz bank", style = MaterialTheme.typography.titleLarge)
            Text(
                "Create, edit, and delete the questions used to unlock blocked apps.",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            OutlinedButton(
                onClick = onOpenQuizBank,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            ) {
                Text("Manage quiz questions")
            }
        }

        Column {
            Text("Backup", style = MaterialTheme.typography.titleLarge)
            Text(
                "Export your blocks, question bank, and cosmos progress to a file, or restore from one.",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            OutlinedButton(
                onClick = { exportLauncher.launch("orbit-backup.json") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            ) {
                Text("Export backup")
            }
            OutlinedButton(
                onClick = { importLauncher.launch(arrayOf("application/json")) },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            ) {
                Text("Import backup")
            }
            val statusText = when (val s = backupStatus) {
                is BackupStatus.Working -> "Working…"
                is BackupStatus.Success -> s.message
                is BackupStatus.Error -> "Error: ${s.message}"
                BackupStatus.Idle -> null
            }
            if (statusText != null) {
                Text(
                    statusText,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (backupStatus is BackupStatus.Error) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }

        Column {
            Text("Developer", style = MaterialTheme.typography.titleLarge)
            Text(
                "Preview the cosmos and its stages with mock data. For testing only.",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            OutlinedButton(
                onClick = onOpenDeveloper,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            ) {
                Text("Open developer preview")
            }
        }

    }
}

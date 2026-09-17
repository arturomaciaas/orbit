package com.orbit.blocker.ui.onboarding

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.orbit.blocker.service.AccessibilityPermission
import com.orbit.blocker.service.NotificationAccessPermission

/**
 * First-run flow. Explains Orbit and guides the two core permission grants
 * (Accessibility for blocking, Notification access for filtering), plus the
 * POST_NOTIFICATIONS runtime permission on Android 13+. Re-checks grant state on
 * resume so the checkmarks update when the user returns from system settings.
 */
@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    var accessibilityGranted by remember { mutableStateOf(AccessibilityPermission.isEnabled(context)) }
    var notificationAccessGranted by remember { mutableStateOf(NotificationAccessPermission.isEnabled(context)) }
    var postNotifGranted by remember { mutableStateOf(hasPostNotifications(context)) }

    val postNotifLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted -> postNotifGranted = granted }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                accessibilityGranted = AccessibilityPermission.isEnabled(context)
                notificationAccessGranted = NotificationAccessPermission.isEnabled(context)
                postNotifGranted = hasPostNotifications(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxSize()) {
        com.orbit.blocker.ui.components.SpaceBackground(modifier = Modifier.fillMaxSize())
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text("Welcome to Orbit", style = MaterialTheme.typography.headlineLarge)
        Text(
            "Block distracting apps, gate them behind quiz questions, and grow a cosmos as you stay focused. " +
                "Orbit needs a couple of permissions to work.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 12.dp, bottom = 20.dp),
        )

        PermissionStep(
            title = "Accessibility service",
            rationale = "Lets Orbit notice when you open a blocked app so it can show the quiz gate. Orbit does not read your screen content.",
            granted = accessibilityGranted,
            buttonText = "Open accessibility settings",
            onClick = { AccessibilityPermission.openSettings(context) },
        )

        PermissionStep(
            title = "Notification access",
            rationale = "Lets Orbit filter notifications into priority, peek, and suppressed tiers, and collect them in your digest.",
            granted = notificationAccessGranted,
            buttonText = "Grant notification access",
            onClick = { NotificationAccessPermission.openSettings(context) },
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PermissionStep(
                title = "Show notifications",
                rationale = "Lets Orbit show the focus-session timer and peek previews.",
                granted = postNotifGranted,
                buttonText = "Allow notifications",
                onClick = { postNotifLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS) },
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        val allCore = accessibilityGranted && notificationAccessGranted
        Button(
            onClick = { viewModel.completeOnboarding(onFinished) },
            enabled = true,
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
        ) {
            Text(if (allCore) "Enter Orbit" else "Skip for now")
        }
        if (!allCore) {
            Text(
                "You can grant these later in Settings, but blocking and filtering won't work until you do.",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            )
        }
    }
    }
}

@Composable
private fun PermissionStep(
    title: String,
    rationale: String,
    granted: Boolean,
    buttonText: String,
    onClick: () -> Unit,
) {
    com.orbit.blocker.ui.components.GlassCard(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (granted) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (granted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 12.dp),
                )
            }
            Text(
                rationale,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )
            if (!granted) {
                OutlinedButton(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                ) { Text(buttonText) }
            }
        }
    }
}

private fun hasPostNotifications(context: android.content.Context): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
    return context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) ==
        android.content.pm.PackageManager.PERMISSION_GRANTED
}

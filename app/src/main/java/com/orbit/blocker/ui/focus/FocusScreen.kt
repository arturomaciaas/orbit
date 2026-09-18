package com.orbit.blocker.ui.focus

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.orbit.blocker.data.model.NotificationTier
import com.orbit.blocker.domain.block.DurationFormatter
import com.orbit.blocker.domain.focus.FocusTimer
import com.orbit.blocker.ui.components.GlassCard
import com.orbit.blocker.ui.components.GlassPill
import com.orbit.blocker.ui.components.SectionLabel
import com.orbit.blocker.ui.theme.CometCyan
import com.orbit.blocker.ui.theme.NebulaViolet
import com.orbit.blocker.ui.theme.StarWhite
import kotlinx.coroutines.delay

@Composable
fun FocusScreen(viewModel: FocusViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // React to one-shot start outcomes (e.g. nothing to block).
    LaunchedEffect(Unit) {
        viewModel.startEvents.collect { event ->
            when (event) {
                is FocusStartEvent.NoAppsToBlock -> snackbarHostState.showSnackbar(
                    "No apps to block — every app is on the allow-list, or the app list couldn't be read.",
                )
                is FocusStartEvent.Started -> { /* the active-session UI takes over automatically */ }
            }
        }
    }

    val endsAt = state.session.endsAt ?: 0L

    // The service normally ends a session (flipping session.active off) when the timer hits
    // zero. But if the service process was killed and restored, or the app is reopened with a
    // session that expires while this screen is open, that flip may not arrive — leaving the
    // dial frozen on "expired". So the UI treats the countdown reaching zero as authoritative:
    // once complete, we drop the active view and return to setup on our own.
    var timerComplete by remember(endsAt) {
        mutableStateOf(state.session.active && FocusTimer.isComplete(endsAt, System.currentTimeMillis()))
    }
    val showActive = state.session.active && !timerComplete

    Box(modifier = Modifier.fillMaxSize()) {
        // The active-session experience takes over the whole screen.
        AnimatedVisibility(visible = showActive, enter = fadeIn(), exit = fadeOut()) {
            ActiveSession(
                endsAt = endsAt,
                startedAt = state.session.startedAt ?: 0L,
                blockedCount = state.session.blockedPackages.size,
                onComplete = {
                    timerComplete = true
                    viewModel.onSessionTimerExpired()
                },
            )
        }

        if (!showActive) {
            SetupContent(
                state = state,
                onAddAllowed = viewModel::addAllowedApp,
                onRemoveAllowed = viewModel::removeAllowedApp,
                onSetTier = viewModel::setNotificationTier,
                onStart = { minutes -> viewModel.startSession(context, minutes) },
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

// region setup
@Composable
private fun SetupContent(
    state: FocusUiState,
    onAddAllowed: (com.orbit.blocker.data.apps.InstalledApp) -> Unit,
    onRemoveAllowed: (String) -> Unit,
    onSetTier: (String, NotificationTier) -> Unit,
    onStart: (Int) -> Unit,
) {
    var minutes by remember { mutableStateOf(25) }
    var showPicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(16.dp))
        Text(
            "Focus",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            "Everything is locked behind the quiz gate for the time you set. Keep a few apps open below.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp, bottom = 8.dp),
        )

        // Section 1: the dial.
        Spacer(Modifier.height(12.dp))
        OrbitDial(
            minutes = minutes,
            onMinutesChange = { minutes = it },
            diameter = 260.dp,
        )

        // Start button sits directly under the dial. Disabled until the installed-app list
        // has loaded, so a session can never start with an empty (blocks-nothing) package set.
        Spacer(Modifier.height(8.dp))
        StartButton(enabled = !state.loading, onClick = { onStart(minutes) })

        // Section 2: apps that stay open (the allow-list).
        Spacer(Modifier.height(24.dp))
        GlassCard(modifier = Modifier.fillMaxWidth().widthIn(max = 520.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SectionLabel("Apps that stay open")
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add an app to keep open",
                    tint = CometCyan,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { showPicker = true }
                        .padding(4.dp),
                )
            }
            Text(
                "Blocked by default means everything else. These few are exempt.",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
            )
            if (state.allowedApps.isEmpty()) {
                Text(
                    "No exceptions — every app will be gated.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            } else {
                state.allowedApps.forEach { app ->
                    AllowedAppRow(
                        app = app,
                        onRemove = { onRemoveAllowed(app.packageName) },
                        onSetTier = { tier -> onSetTier(app.packageName, tier) },
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }

    if (showPicker) {
        AppPickerSheet(
            installed = state.installedApps.filter { it.packageName !in state.allowedPackages },
            onPick = {
                onAddAllowed(it)
                showPicker = false
            },
            onDismiss = { showPicker = false },
        )
    }
}

@Composable
private fun AllowedAppRow(
    app: AllowedAppUi,
    onRemove: () -> Unit,
    onSetTier: (NotificationTier) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                app.label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Remove ${app.label}",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(onClick = onRemove)
                    .padding(2.dp)
                    .size(18.dp),
            )
        }
        // Notification tier — how this open app's notifications reach you.
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 8.dp),
        ) {
            NotificationTier.entries.forEach { tier ->
                GlassPill(
                    text = tier.chipLabel(),
                    selected = app.notificationTier == tier,
                    onClick = { onSetTier(tier) },
                )
            }
        }
    }
}

@Composable
private fun StartButton(enabled: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(50)
    val gradient = if (enabled) {
        Brush.horizontalGradient(listOf(CometCyan, NebulaViolet))
    } else {
        Brush.horizontalGradient(listOf(CometCyan.copy(alpha = 0.4f), NebulaViolet.copy(alpha = 0.4f)))
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 520.dp)
            .clip(shape)
            .background(gradient, shape)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            if (enabled) "Start focus session" else "Loading apps…",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

private fun NotificationTier.chipLabel(): String = when (this) {
    NotificationTier.PRIORITY -> "Priority"
    NotificationTier.PEEK -> "Peek"
    NotificationTier.SUPPRESS -> "Suppress"
}
// endregion

// region active session
@Composable
private fun ActiveSession(
    endsAt: Long,
    startedAt: Long,
    blockedCount: Int,
    onComplete: () -> Unit,
) {
    var now by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffectTicker(endsAt, onTick = { now = it }, onComplete = onComplete)

    val remaining = FocusTimer.remainingMillis(endsAt, now)
    val progress = FocusTimer.progressFraction(startedAt, endsAt, now)

    // A soft, slow-breathing aura behind the countdown to make the state feel alive.
    val transition = rememberInfiniteTransition(label = "aura")
    val aura by transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(3200, easing = LinearEasing), RepeatMode.Reverse),
        label = "auraScale",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SectionLabel("In orbit")
        Spacer(Modifier.height(24.dp))

        Box(contentAlignment = Alignment.Center) {
            // Breathing glow.
            Box(
                modifier = Modifier
                    .size((300 * aura).dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(NebulaViolet.copy(alpha = 0.18f), Color.Transparent),
                        ),
                    ),
            )
            OrbitCountdownDial(
                progress = progress,
                centerLabel = DurationFormatter.format(remaining),
                centerSub = "remaining",
                diameter = 300.dp,
            )
        }

        Spacer(Modifier.height(28.dp))
        Text(
            if (blockedCount > 0) "$blockedCount apps are locked" else "Everything is locked",
            style = MaterialTheme.typography.titleMedium,
            color = StarWhite,
        )
        Text(
            "Amaze, amaze, amaze!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

/**
 * Ticks [onTick] with the current time every second until the session ends, then fires
 * [onComplete] exactly once. [onComplete] lets the screen fall back to setup even if the
 * session's own "active" flag never flips (e.g. the countdown expired while the service
 * wasn't around to end it).
 */
@Composable
private fun LaunchedEffectTicker(endsAt: Long, onTick: (Long) -> Unit, onComplete: () -> Unit) {
    androidx.compose.runtime.LaunchedEffect(endsAt) {
        while (true) {
            val current = System.currentTimeMillis()
            onTick(current)
            if (FocusTimer.isComplete(endsAt, current)) {
                onComplete()
                break
            }
            delay(1_000)
        }
    }
}
// endregion

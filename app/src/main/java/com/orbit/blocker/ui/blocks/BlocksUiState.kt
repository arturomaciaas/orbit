package com.orbit.blocker.ui.blocks

import com.orbit.blocker.data.apps.InstalledApp
import com.orbit.blocker.data.model.BlockMode
import com.orbit.blocker.data.model.BlockRule

/** A managed app joined with its active rules, for display in the Blocks screen. */
data class ManagedAppUi(
    val packageName: String,
    val label: String,
    val notificationTier: com.orbit.blocker.data.model.NotificationTier,
    val rules: List<BlockRule>,
) {
    val hasDurationBlock: Boolean get() = rules.any { it.mode == BlockMode.DURATION && it.enabled }
    val hasFocusBlock: Boolean get() = rules.any { it.mode == BlockMode.FOCUS_SESSION && it.enabled }
}

data class BlocksUiState(
    val loading: Boolean = true,
    val query: String = "",
    val installedApps: List<InstalledApp> = emptyList(),
    val managedApps: List<ManagedAppUi> = emptyList(),
) {
    /** Installed apps filtered by the current search query. */
    val filteredInstalledApps: List<InstalledApp>
        get() = if (query.isBlank()) installedApps
        else installedApps.filter { it.label.contains(query, ignoreCase = true) }
}

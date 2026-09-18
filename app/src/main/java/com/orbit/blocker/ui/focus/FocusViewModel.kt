package com.orbit.blocker.ui.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbit.blocker.data.apps.InstalledApp
import com.orbit.blocker.data.apps.InstalledAppsProvider
import com.orbit.blocker.data.model.BlockedApp
import com.orbit.blocker.data.model.NotificationTier
import com.orbit.blocker.data.repository.BlockRepository
import com.orbit.blocker.domain.focus.FocusFavorites
import com.orbit.blocker.domain.focus.FocusSessionManager
import com.orbit.blocker.domain.focus.FocusSessionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** An app the user keeps open during focus sessions, with its notification tier. */
data class AllowedAppUi(
    val packageName: String,
    val label: String,
    val notificationTier: NotificationTier,
    val isDefaultFavorite: Boolean,
)

data class FocusUiState(
    val loading: Boolean = true,
    /** Apps that stay open during a session (the allow-list). */
    val allowedApps: List<AllowedAppUi> = emptyList(),
    /** All launchable apps on the device, for the "add to allow-list" picker. */
    val installedApps: List<InstalledApp> = emptyList(),
    val session: FocusSessionState = FocusSessionState.INACTIVE,
) {
    val allowedPackages: Set<String> get() = allowedApps.map { it.packageName }.toSet()

    /** Everything that isn't explicitly allowed gets blocked during a session. */
    fun blockedPackages(): Set<String> =
        installedApps.map { it.packageName }.toSet() - allowedPackages
}

@HiltViewModel
class FocusViewModel @Inject constructor(
    private val blockRepository: BlockRepository,
    private val installedAppsProvider: InstalledAppsProvider,
    private val focusSessionManager: FocusSessionManager,
) : ViewModel() {

    private val installedApps = MutableStateFlow<List<InstalledApp>>(emptyList())
    private val loading = MutableStateFlow(true)

    val uiState: StateFlow<FocusUiState> = combine(
        loading,
        installedApps,
        blockRepository.observeManagedApps(),
        focusSessionManager.state,
    ) { loading, installed, managed, session ->
        // Managed apps are the "kept open" allow-list. We remember which packages match a
        // default favorite so the UI can badge them.
        val defaultFavoritePkgs = FocusFavorites.defaultAllowedPackages(installed)
        val allowed = managed
            .map { app ->
                AllowedAppUi(
                    packageName = app.packageName,
                    label = app.appLabel,
                    notificationTier = app.notificationTier,
                    isDefaultFavorite = app.packageName in defaultFavoritePkgs,
                )
            }
            .sortedBy { it.label.lowercase() }
        FocusUiState(
            loading = loading,
            allowedApps = allowed,
            installedApps = installed,
            session = session,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FocusUiState(),
    )

    init {
        loadApps()
    }

    /** Loads installed apps and, on first run, seeds the allow-list with default favorites. */
    private fun loadApps() {
        viewModelScope.launch {
            loading.value = true
            val apps = installedAppsProvider.loadLaunchableApps()
            installedApps.value = apps
            seedDefaultFavoritesIfEmpty(apps)
            loading.value = false
        }
    }

    private suspend fun seedDefaultFavoritesIfEmpty(installed: List<InstalledApp>) {
        // Only seed when the user has no allow-list yet, so we never override their choices.
        val alreadyManaged = blockRepository.observeManagedApps().first()
        if (alreadyManaged.isNotEmpty()) return
        val defaults = FocusFavorites.defaultAllowedPackages(installed)
        if (defaults.isEmpty()) return
        installed.filter { it.packageName in defaults }.forEach { app ->
            blockRepository.addManagedApp(
                BlockedApp(
                    packageName = app.packageName,
                    appLabel = app.label,
                    notificationTier = NotificationTier.PRIORITY,
                )
            )
        }
    }

    fun addAllowedApp(app: InstalledApp) {
        viewModelScope.launch {
            blockRepository.addManagedApp(
                BlockedApp(
                    packageName = app.packageName,
                    appLabel = app.label,
                    notificationTier = NotificationTier.PRIORITY,
                )
            )
        }
    }

    fun removeAllowedApp(packageName: String) {
        viewModelScope.launch { blockRepository.removeManagedApp(packageName) }
    }

    fun setNotificationTier(packageName: String, tier: NotificationTier) {
        viewModelScope.launch { blockRepository.setNotificationTier(packageName, tier) }
    }
}

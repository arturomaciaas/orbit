package com.orbit.blocker.ui.focus

import android.content.Context
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
import com.orbit.blocker.service.FocusSessionService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * One-shot outcome of a start request. The UI reacts to these; the session itself is started
 * here in the ViewModel (not the composable) so the blocked-app set is computed from a freshly
 * loaded installed-apps list rather than whatever async state the UI happens to hold.
 */
sealed interface FocusStartEvent {
    /** Session started; [blockedCount] apps will be gated. The service was told to begin. */
    data class Started(val blockedCount: Int) : FocusStartEvent
    /** Could not start because there were no apps to block (empty installed list or all allowed). */
    data object NoAppsToBlock : FocusStartEvent
}

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

    private val _startEvents = MutableSharedFlow<FocusStartEvent>(extraBufferCapacity = 1)
    /** One-shot start outcomes for the UI to react to (e.g. show a "nothing to block" message). */
    val startEvents: SharedFlow<FocusStartEvent> = _startEvents.asSharedFlow()

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

    /**
     * Starts a focus session for [minutes]. The blocked-app set is computed HERE, from a
     * guaranteed-loaded installed-apps list minus the current allow-list, rather than from
     * whatever the UI state holds. This fixes the bug where tapping Start before the async
     * app load finished persisted an empty session that blocked nothing.
     *
     * If the set would be empty (no installed apps could be read, or every app is allowed),
     * the session is NOT started and [FocusStartEvent.NoAppsToBlock] is emitted.
     */
    fun startSession(context: Context, minutes: Int) {
        viewModelScope.launch {
            // Use the cached list if we already loaded it; otherwise load fresh so we never
            // compute the blocked set from an empty placeholder.
            val installed = installedApps.value.ifEmpty {
                installedAppsProvider.loadLaunchableApps().also { installedApps.value = it }
            }
            // Read the allow-list authoritatively from the repository (not UI-derived state).
            val allowed = blockRepository.observeManagedApps().first()
                .map { it.packageName }
                .toSet()
            val blocked = installed.map { it.packageName }.toSet() - allowed

            if (blocked.isEmpty()) {
                _startEvents.emit(FocusStartEvent.NoAppsToBlock)
                return@launch
            }

            FocusSessionService.start(
                context = context,
                packages = blocked,
                durationMillis = TimeUnit.MINUTES.toMillis(minutes.toLong()),
            )
            _startEvents.emit(FocusStartEvent.Started(blocked.size))
        }
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

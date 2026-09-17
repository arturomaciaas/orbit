package com.orbit.blocker.ui.blocks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbit.blocker.data.apps.InstalledApp
import com.orbit.blocker.data.apps.InstalledAppsProvider
import com.orbit.blocker.data.model.BlockedApp
import com.orbit.blocker.data.repository.BlockRepository
import com.orbit.blocker.domain.block.BlockRuleFactory
import com.orbit.blocker.domain.block.DurationUnit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BlocksViewModel @Inject constructor(
    private val blockRepository: BlockRepository,
    private val installedAppsProvider: InstalledAppsProvider,
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val installedApps = MutableStateFlow<List<InstalledApp>>(emptyList())
    private val loading = MutableStateFlow(true)

    // Join managed apps + their rules into display models.
    private val managedApps = combine(
        blockRepository.observeManagedApps(),
        blockRepository.observeAllRules(),
    ) { apps, rules ->
        val rulesByPackage = rules.groupBy { it.packageName }
        apps.map { app ->
            ManagedAppUi(
                packageName = app.packageName,
                label = app.appLabel,
                notificationTier = app.notificationTier,
                rules = rulesByPackage[app.packageName].orEmpty(),
            )
        }
    }

    val uiState: StateFlow<BlocksUiState> = combine(
        loading,
        query,
        installedApps,
        managedApps,
    ) { loading, query, installed, managed ->
        BlocksUiState(
            loading = loading,
            query = query,
            installedApps = installed,
            managedApps = managed,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BlocksUiState(),
    )

    init {
        refreshInstalledApps()
    }

    fun refreshInstalledApps() {
        viewModelScope.launch {
            loading.value = true
            installedApps.value = installedAppsProvider.loadLaunchableApps()
            loading.value = false
        }
    }

    fun onQueryChange(value: String) {
        query.value = value
    }

    /** Adds a duration block for [app], creating the managed-app record if needed. */
    fun addDurationBlock(app: InstalledApp, amount: Long, unit: DurationUnit) {
        viewModelScope.launch {
            ensureManaged(app)
            val rule = BlockRuleFactory.durationRule(app.packageName, amount, unit)
            blockRepository.upsertRule(rule)
        }
    }

    /** Adds a focus-session block for [app]. */
    fun addFocusBlock(app: InstalledApp) {
        viewModelScope.launch {
            ensureManaged(app)
            // Avoid duplicate focus rules for the same package.
            val existing = blockRepository.enabledRulesForPackage(app.packageName)
            if (existing.none { it.mode == com.orbit.blocker.data.model.BlockMode.FOCUS_SESSION }) {
                blockRepository.upsertRule(BlockRuleFactory.focusSessionRule(app.packageName))
            }
        }
    }

    fun removeRule(ruleId: Long) {
        viewModelScope.launch { blockRepository.deleteRule(ruleId) }
    }

    fun setNotificationTier(packageName: String, tier: com.orbit.blocker.data.model.NotificationTier) {
        viewModelScope.launch { blockRepository.setNotificationTier(packageName, tier) }
    }

    fun removeManagedApp(packageName: String) {
        viewModelScope.launch { blockRepository.removeManagedApp(packageName) }
    }

    private suspend fun ensureManaged(app: InstalledApp) {
        blockRepository.addManagedApp(
            BlockedApp(packageName = app.packageName, appLabel = app.label)
        )
    }
}

package com.orbit.blocker.domain.block

import com.orbit.blocker.data.model.BlockRule
import com.orbit.blocker.data.repository.AccessGrantRepository
import com.orbit.blocker.data.repository.BlockRepository
import com.orbit.blocker.domain.focus.FocusSessionState
import com.orbit.blocker.domain.focus.FocusSessionStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Runtime coordinator for interception. Keeps an in-memory cache of enabled block
 * rules (updated reactively from the DB) so the AccessibilityService can decide
 * quickly on each foreground change without a synchronous DB read. Delegates the
 * actual yes/no to the pure [GateDecision].
 */
@Singleton
class BlockEnforcer @Inject constructor(
    private val blockRepository: BlockRepository,
    private val accessGrantRepository: AccessGrantRepository,
    private val focusSessionStore: FocusSessionStore,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val cachedRules = MutableStateFlow<List<BlockRule>>(emptyList())

    // The durable active focus session, kept fresh from persistence. Reading it directly (rather
    // than an in-memory holder) is what makes focus enforcement survive the accessibility
    // service being recreated in a new process mid-session.
    private val focusSession = MutableStateFlow(FocusSessionState.INACTIVE)

    /** Starts observing enabled rules and the persisted focus session. Safe to call repeatedly. */
    fun startObserving() {
        blockRepository.observeEnabledRules()
            .onEach { cachedRules.value = it }
            .launchIn(scope)
        focusSessionStore.activeFocusSession
            .onEach { focusSession.value = it }
            .launchIn(scope)
    }

    /**
     * Decides whether to gate [packageName] right now. Access-grant lookup is a
     * suspend DB call (cheap, indexed) so the caller must invoke from a coroutine.
     */
    suspend fun shouldGate(packageName: String, now: Long = System.currentTimeMillis()): Boolean {
        val rules = cachedRules.value.filter { it.packageName == packageName }
        // Treat a session whose end time has passed as inactive, in case cleanup hasn't run yet.
        val focus = focusSession.value.takeUnless { it.endsAt != null && it.endsAt <= now }
            ?: FocusSessionState.INACTIVE
        // Fast path: nothing references this package. A package is "referenced" if it has a
        // rule OR the active focus session covers it (block-all-by-default sessions carry the
        // package set directly, so most gated apps won't have a stored rule).
        if (rules.isEmpty() && !focus.isPackageBlocked(packageName)) return false

        val hasGrant = accessGrantRepository.hasActiveGrant(packageName, now)
        return GateDecision.shouldGate(
            packageName = packageName,
            rules = rules,
            focus = focus,
            hasActiveGrant = hasGrant,
            now = now,
        )
    }
}

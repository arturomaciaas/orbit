package com.orbit.blocker.domain.focus

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

/**
 * App-wide holder for the current focus session. The interception engine reads [state]
 * (and [current]) to know which packages are focus-blocked right now.
 *
 * The active session is persisted via [FocusSessionStore] so it survives process death: the
 * accessibility service that enforces blocking has an independent, system-managed lifecycle
 * and can be recreated in a fresh process mid-session. Without persistence its in-memory
 * snapshot resets to [FocusSessionState.INACTIVE] and every app is wrongly allowed through.
 * On construction we rehydrate from the persisted value (auto-expiring a session whose end
 * time has already passed), and [start]/[stop] write through to persistence.
 */
@Singleton
class FocusSessionManager @Inject constructor(
    private val store: FocusSessionStore,
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _state = MutableStateFlow(restoreInitialState())
    val state: StateFlow<FocusSessionState> = _state.asStateFlow()

    fun current(): FocusSessionState = _state.value

    fun start(blockedPackages: Set<String>, startedAt: Long, endsAt: Long) {
        val newState = FocusSessionState(
            active = true,
            blockedPackages = blockedPackages,
            startedAt = startedAt,
            endsAt = endsAt,
        )
        _state.value = newState
        scope.launch { store.saveActiveFocusSession(newState) }
    }

    fun stop() {
        _state.value = FocusSessionState.INACTIVE
        scope.launch { store.clearActiveFocusSession() }
    }

    /**
     * Reads the persisted session synchronously at construction. A session whose [endsAt] is
     * already in the past is treated as inactive (and cleared), so a stale record left behind
     * by a crash or force-stop can't block apps forever. The blocking read is a single small
     * DataStore lookup performed once when the singleton is created.
     */
    private fun restoreInitialState(): FocusSessionState {
        val persisted = runCatching {
            runBlocking { store.activeFocusSession.first() }
        }.getOrDefault(FocusSessionState.INACTIVE)

        if (!persisted.active) return FocusSessionState.INACTIVE

        val endsAt = persisted.endsAt
        val expired = endsAt != null && endsAt <= System.currentTimeMillis()
        if (expired) {
            scope.launch { store.clearActiveFocusSession() }
            return FocusSessionState.INACTIVE
        }
        return persisted
    }
}

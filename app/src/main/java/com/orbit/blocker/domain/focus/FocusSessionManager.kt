package com.orbit.blocker.domain.focus

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * App-wide holder for the current focus session. The interception engine reads
 * [state] to know which packages are focus-blocked right now. The full timer,
 * foreground service, and completion events are layered on in Task 5.
 */
@Singleton
class FocusSessionManager @Inject constructor() {

    private val _state = MutableStateFlow(FocusSessionState.INACTIVE)
    val state: StateFlow<FocusSessionState> = _state.asStateFlow()

    fun current(): FocusSessionState = _state.value

    fun start(blockedPackages: Set<String>, startedAt: Long, endsAt: Long) {
        _state.value = FocusSessionState(
            active = true,
            blockedPackages = blockedPackages,
            startedAt = startedAt,
            endsAt = endsAt,
        )
    }

    fun stop() {
        _state.update { FocusSessionState.INACTIVE }
    }
}

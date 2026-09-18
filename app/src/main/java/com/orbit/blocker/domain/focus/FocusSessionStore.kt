package com.orbit.blocker.domain.focus

import kotlinx.coroutines.flow.Flow

/**
 * Durable persistence for the active focus session, abstracted so [FocusSessionManager] does
 * not depend directly on the DataStore-backed settings (and stays trivially unit-testable).
 * Implemented by the settings layer.
 */
interface FocusSessionStore {
    /** The persisted active session, or [FocusSessionState.INACTIVE] when none is stored. */
    val activeFocusSession: Flow<FocusSessionState>

    /** Persists [state] as the active session. */
    suspend fun saveActiveFocusSession(state: FocusSessionState)

    /** Clears any persisted active session. */
    suspend fun clearActiveFocusSession()
}

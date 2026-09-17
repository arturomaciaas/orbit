package com.orbit.blocker.domain.focus

/**
 * Snapshot of the current focus session, shared between the timer (Task 5) and the
 * interception decision engine (Task 4). Kept as a plain data class so it is trivial
 * to construct in tests.
 */
data class FocusSessionState(
    val active: Boolean = false,
    /** Packages blocked for the duration of the active session. */
    val blockedPackages: Set<String> = emptySet(),
    /** Epoch millis when the session started; null when inactive. */
    val startedAt: Long? = null,
    /** Epoch millis when the session ends; null when inactive. */
    val endsAt: Long? = null,
) {
    fun isPackageBlocked(packageName: String): Boolean = active && packageName in blockedPackages

    companion object {
        val INACTIVE = FocusSessionState()
    }
}

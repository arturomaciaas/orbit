package com.orbit.blocker.domain.focus

/** Pure countdown math for a focus session, testable without Android. */
object FocusTimer {

    /** Remaining millis between [now] and [endsAt], floored at 0. */
    fun remainingMillis(endsAt: Long, now: Long): Long = (endsAt - now).coerceAtLeast(0L)

    /** True once [now] has reached or passed [endsAt]. */
    fun isComplete(endsAt: Long, now: Long): Boolean = now >= endsAt

    /**
     * Elapsed fraction in 0f..1f given the session [startedAt] and [endsAt].
     * Returns 1f for a zero/negative-length window to avoid division by zero.
     */
    fun progressFraction(startedAt: Long, endsAt: Long, now: Long): Float {
        val total = endsAt - startedAt
        if (total <= 0L) return 1f
        val elapsed = (now - startedAt).coerceIn(0L, total)
        return elapsed.toFloat() / total.toFloat()
    }
}

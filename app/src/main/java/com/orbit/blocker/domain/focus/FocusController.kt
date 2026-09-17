package com.orbit.blocker.domain.focus

import com.orbit.blocker.data.model.FocusOutcome
import com.orbit.blocker.data.model.FocusSessionRecord
import com.orbit.blocker.data.repository.FocusSessionRepository
import com.orbit.blocker.domain.gamification.GamificationEvents
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Coordinates the lifecycle of a focus session independent of the Android service:
 * updates [FocusSessionManager] state, writes a history record when the session ends,
 * and fires the gamification growth event on successful completion.
 *
 * The foreground service owns the ticking clock and notification; it calls
 * [begin]/[complete]/[abort] here.
 */
@Singleton
class FocusController @Inject constructor(
    private val focusSessionManager: FocusSessionManager,
    private val focusSessionRepository: FocusSessionRepository,
    private val gamificationEvents: GamificationEvents,
) {
    fun begin(blockedPackages: Set<String>, startedAt: Long, endsAt: Long) {
        focusSessionManager.start(blockedPackages, startedAt, endsAt)
    }

    /** Called when the timer reaches zero. Records a COMPLETED session and grows the galaxy. */
    suspend fun complete(now: Long = System.currentTimeMillis()) {
        val state = focusSessionManager.current()
        if (!state.active) return
        val started = state.startedAt ?: now
        val planned = (state.endsAt ?: now) - started
        focusSessionManager.stop()
        focusSessionRepository.record(
            FocusSessionRecord(
                startedAt = started,
                endedAt = now,
                plannedDurationMillis = planned,
                outcome = FocusOutcome.COMPLETED,
                blockedPackageCount = state.blockedPackages.size,
            )
        )
        gamificationEvents.onFocusSessionCompleted()
    }

    /** Called when the user cancels early. Records an ABORTED session; no growth. */
    suspend fun abort(now: Long = System.currentTimeMillis()) {
        val state = focusSessionManager.current()
        if (!state.active) return
        val started = state.startedAt ?: now
        val planned = (state.endsAt ?: now) - started
        focusSessionManager.stop()
        focusSessionRepository.record(
            FocusSessionRecord(
                startedAt = started,
                endedAt = now,
                plannedDurationMillis = planned,
                outcome = FocusOutcome.ABORTED,
                blockedPackageCount = state.blockedPackages.size,
            )
        )
    }
}

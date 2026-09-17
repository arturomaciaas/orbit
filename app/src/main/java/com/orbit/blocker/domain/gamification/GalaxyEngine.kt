package com.orbit.blocker.domain.gamification

import com.orbit.blocker.data.model.GalaxyProgress
import com.orbit.blocker.data.model.GalaxyStage
import java.util.concurrent.TimeUnit

/**
 * Pure gamification math for the space-themed progression, decoupled from Room and
 * Android so it is fully unit-testable.
 *
 * Model: [GalaxyProgress.progress] is a 0f..1f value within the current [GalaxyProgress.stage].
 * Completing a focus session adds [GROWTH_PER_SESSION]; crossing 1.0 advances to the next
 * stage (carrying the remainder), capped at the final [GalaxyStage.GALAXY]. A wrong quiz
 * answer applies a partial [SETBACK_PER_WRONG] meteor strike, which can drop the stage but
 * never below PLANET/0.
 */
object GalaxyEngine {

    /** Progress gained per completed focus session (each stage needs ~4 sessions). */
    const val GROWTH_PER_SESSION = 0.25f

    /** Progress lost per wrong answer (partial setback, not a full reset). */
    const val SETBACK_PER_WRONG = 0.15f

    private val STAGES = GalaxyStage.entries

    /**
     * Applies growth for one completed focus session, updating stage/progress, session
     * count, and streak. [now] and [dayOf] allow deterministic testing of streak logic.
     */
    fun applySessionCompleted(
        current: GalaxyProgress,
        now: Long = System.currentTimeMillis(),
    ): GalaxyProgress {
        val (stage, progress) = advance(current.stage, current.progress + GROWTH_PER_SESSION)
        val streak = nextStreak(current.currentStreakDays, current.lastSessionCompletedAt, now)
        return current.copy(
            stage = stage,
            progress = progress,
            totalSessionsCompleted = current.totalSessionsCompleted + 1,
            currentStreakDays = streak,
            longestStreakDays = maxOf(current.longestStreakDays, streak),
            lastSessionCompletedAt = now,
        )
    }

    /** Applies a meteor setback for [wrongCount] wrong answers. */
    fun applyMeteorStrike(current: GalaxyProgress, wrongCount: Int): GalaxyProgress {
        if (wrongCount <= 0) return current
        val (stage, progress) = regress(
            current.stage,
            current.progress - SETBACK_PER_WRONG * wrongCount,
        )
        return current.copy(
            stage = stage,
            progress = progress,
            meteorStrikes = current.meteorStrikes + wrongCount,
        )
    }

    /**
     * Overall completion across all stages in 0f..1f, useful for a single progress ring.
     * PLANET at 0.0 progress = 0f; GALAXY at 1.0 progress = 1f.
     */
    fun overallFraction(progress: GalaxyProgress): Float {
        val stageCount = STAGES.size // 5 stages
        val stageIndex = progress.stage.ordinal
        val perStage = 1f / stageCount
        return (stageIndex * perStage + progress.progress.coerceIn(0f, 1f) * perStage)
            .coerceIn(0f, 1f)
    }

    // region internal
    /** Advances stage/progress upward, carrying remainder; caps at GALAXY 1.0. */
    private fun advance(stage: GalaxyStage, rawProgress: Float): Pair<GalaxyStage, Float> {
        var index = stage.ordinal
        var progress = rawProgress
        while (progress >= 1f && index < STAGES.lastIndex) {
            progress -= 1f
            index++
        }
        // If at the final stage, clamp progress to 1.0 (fully grown).
        if (index == STAGES.lastIndex && progress > 1f) progress = 1f
        return STAGES[index] to progress.coerceIn(0f, 1f)
    }

    /** Regresses stage/progress downward on setback; floors at PLANET 0.0. */
    private fun regress(stage: GalaxyStage, rawProgress: Float): Pair<GalaxyStage, Float> {
        var index = stage.ordinal
        var progress = rawProgress
        while (progress < 0f && index > 0) {
            index--
            progress += 1f
        }
        if (index == 0 && progress < 0f) progress = 0f
        return STAGES[index] to progress.coerceIn(0f, 1f)
    }

    /**
     * Streak logic based on calendar-day gaps:
     *  - same day as last completion => streak unchanged (min 1)
     *  - exactly the next day => streak + 1
     *  - a longer gap (or first ever) => reset to 1
     */
    private fun nextStreak(currentStreak: Int, lastAt: Long?, now: Long): Int {
        if (lastAt == null) return 1
        val lastDay = lastAt / DAY_MILLIS
        val nowDay = now / DAY_MILLIS
        return when (nowDay - lastDay) {
            0L -> currentStreak.coerceAtLeast(1)
            1L -> currentStreak + 1
            else -> 1
        }
    }

    private val DAY_MILLIS = TimeUnit.DAYS.toMillis(1)
    // endregion
}

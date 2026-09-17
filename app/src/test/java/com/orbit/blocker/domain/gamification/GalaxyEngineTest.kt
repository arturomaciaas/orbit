package com.orbit.blocker.domain.gamification

import com.google.common.truth.Truth.assertThat
import com.orbit.blocker.data.model.GalaxyProgress
import com.orbit.blocker.data.model.GalaxyStage
import org.junit.Test
import java.util.concurrent.TimeUnit

class GalaxyEngineTest {

    private val day = TimeUnit.DAYS.toMillis(1)

    @Test
    fun sessionAddsGrowthWithinStage() {
        val result = GalaxyEngine.applySessionCompleted(GalaxyProgress(), now = day)
        assertThat(result.stage).isEqualTo(GalaxyStage.PLANET)
        assertThat(result.progress).isWithin(1e-4f).of(GalaxyEngine.GROWTH_PER_SESSION)
        assertThat(result.totalSessionsCompleted).isEqualTo(1)
    }

    @Test
    fun crossingOneAdvancesStageCarryingRemainder() {
        // 0.25 growth * 5 sessions = 1.25 -> MOON at 0.25
        var p = GalaxyProgress()
        repeat(5) { p = GalaxyEngine.applySessionCompleted(p, now = day * (it + 1)) }
        assertThat(p.stage).isEqualTo(GalaxyStage.MOON)
        assertThat(p.progress).isWithin(1e-4f).of(0.25f)
        assertThat(p.totalSessionsCompleted).isEqualTo(5)
    }

    @Test
    fun finalStageClampsAtFullyGrown() {
        var p = GalaxyProgress(stage = GalaxyStage.GALAXY, progress = 0.9f)
        repeat(5) { p = GalaxyEngine.applySessionCompleted(p, now = day * (it + 1)) }
        assertThat(p.stage).isEqualTo(GalaxyStage.GALAXY)
        assertThat(p.progress).isEqualTo(1f)
    }

    @Test
    fun meteorReducesProgressPartially() {
        val start = GalaxyProgress(stage = GalaxyStage.RINGS, progress = 0.5f)
        val result = GalaxyEngine.applyMeteorStrike(start, wrongCount = 1)
        assertThat(result.stage).isEqualTo(GalaxyStage.RINGS)
        assertThat(result.progress).isWithin(1e-4f).of(0.5f - GalaxyEngine.SETBACK_PER_WRONG)
        assertThat(result.meteorStrikes).isEqualTo(1)
    }

    @Test
    fun meteorCanDropStageButNotBelowPlanetZero() {
        val start = GalaxyProgress(stage = GalaxyStage.MOON, progress = 0.05f)
        // 0.05 - 0.15 = -0.10 -> drop to PLANET at 0.90
        val dropped = GalaxyEngine.applyMeteorStrike(start, wrongCount = 1)
        assertThat(dropped.stage).isEqualTo(GalaxyStage.PLANET)
        assertThat(dropped.progress).isWithin(1e-4f).of(0.9f)

        // Big setback at PLANET floors at 0.
        val floored = GalaxyEngine.applyMeteorStrike(GalaxyProgress(), wrongCount = 10)
        assertThat(floored.stage).isEqualTo(GalaxyStage.PLANET)
        assertThat(floored.progress).isEqualTo(0f)
    }

    @Test
    fun meteorWithZeroWrongIsNoOp() {
        val start = GalaxyProgress(stage = GalaxyStage.SYSTEM, progress = 0.4f)
        assertThat(GalaxyEngine.applyMeteorStrike(start, 0)).isEqualTo(start)
    }

    @Test
    fun streak_incrementsOnConsecutiveDays() {
        var p = GalaxyEngine.applySessionCompleted(GalaxyProgress(), now = day * 10)
        assertThat(p.currentStreakDays).isEqualTo(1)
        p = GalaxyEngine.applySessionCompleted(p, now = day * 11)
        assertThat(p.currentStreakDays).isEqualTo(2)
        assertThat(p.longestStreakDays).isEqualTo(2)
    }

    @Test
    fun streak_unchangedSameDay() {
        var p = GalaxyEngine.applySessionCompleted(GalaxyProgress(), now = day * 10)
        p = GalaxyEngine.applySessionCompleted(p, now = day * 10 + 1000)
        assertThat(p.currentStreakDays).isEqualTo(1)
    }

    @Test
    fun streak_resetsAfterGap() {
        var p = GalaxyEngine.applySessionCompleted(GalaxyProgress(), now = day * 10)
        p = GalaxyEngine.applySessionCompleted(p, now = day * 11)
        assertThat(p.currentStreakDays).isEqualTo(2)
        // Skip a day -> reset to 1, but longest retained.
        p = GalaxyEngine.applySessionCompleted(p, now = day * 14)
        assertThat(p.currentStreakDays).isEqualTo(1)
        assertThat(p.longestStreakDays).isEqualTo(2)
    }

    @Test
    fun overallFraction_spansStages() {
        assertThat(GalaxyEngine.overallFraction(GalaxyProgress())).isEqualTo(0f)
        assertThat(GalaxyEngine.overallFraction(GalaxyProgress(stage = GalaxyStage.GALAXY, progress = 1f)))
            .isEqualTo(1f)
        // MOON (ordinal 1) at 0.0 -> 1/5 = 0.2
        assertThat(GalaxyEngine.overallFraction(GalaxyProgress(stage = GalaxyStage.MOON, progress = 0f)))
            .isWithin(1e-4f).of(0.2f)
    }
}

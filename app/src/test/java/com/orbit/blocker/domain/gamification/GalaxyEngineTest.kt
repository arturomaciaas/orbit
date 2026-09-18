package com.orbit.blocker.domain.gamification

import com.google.common.truth.Truth.assertThat
import com.orbit.blocker.data.model.CompletedPlanet
import com.orbit.blocker.data.model.GalaxyProgress
import com.orbit.blocker.data.model.PlanetStage
import com.orbit.blocker.data.model.PlanetType
import org.junit.Test
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class GalaxyEngineTest {

    private val day = TimeUnit.DAYS.toMillis(1)

    /** A Terran completes at MOON, so it never gains rings — handy for deterministic tests. */
    private fun terran(progress: Float = 0f, stage: PlanetStage = PlanetStage.PLANET) =
        GalaxyProgress(activePlanetType = PlanetType.TERRAN, stage = stage, progress = progress)

    @Test
    fun sessionAddsGrowthWithinStage() {
        val outcome = GalaxyEngine.applySessionCompleted(terran(), now = day)
        assertThat(outcome.planetCompleted).isFalse()
        assertThat(outcome.progress.stage).isEqualTo(PlanetStage.PLANET)
        assertThat(outcome.progress.progress).isWithin(1e-4f).of(GalaxyEngine.GROWTH_PER_SESSION)
        assertThat(outcome.progress.totalSessionsCompleted).isEqualTo(1)
    }

    @Test
    fun crossingOneAdvancesStageCarryingRemainder() {
        // 0.25 * 5 = 1.25 -> MOON at 0.25 (Terran's final stage, not yet complete since <1 within it).
        var p = terran()
        repeat(5) { p = GalaxyEngine.applySessionCompleted(p, now = day * (it + 1)).progress }
        assertThat(p.stage).isEqualTo(PlanetStage.MOON)
        assertThat(p.progress).isWithin(1e-4f).of(0.25f)
        assertThat(p.totalSessionsCompleted).isEqualTo(5)
    }

    @Test
    fun planetCompletesAtFinalStageAndLocksIn() {
        // Terran final stage = MOON. Get to MOON at 0.75, one more session (=1.0) completes it.
        var p = terran(stage = PlanetStage.MOON, progress = 0.75f)
        val outcome = GalaxyEngine.applySessionCompleted(p, now = day, random = Random(1))
        assertThat(outcome.planetCompleted).isTrue()
        assertThat(outcome.systemCompleted).isFalse()
        assertThat(outcome.lockedPlanetType).isEqualTo(PlanetType.TERRAN)
        assertThat(outcome.lockedSlot).isEqualTo(0)
        assertThat(outcome.lockedSystemIndex).isEqualTo(0)
        // A fresh active planet starts from PLANET/0 and the system count ticks up.
        assertThat(outcome.progress.stage).isEqualTo(PlanetStage.PLANET)
        assertThat(outcome.progress.progress).isEqualTo(0f)
        assertThat(outcome.progress.planetsInSystem).isEqualTo(1)
    }

    @Test
    fun eighthPlanetCompletesSystemAndStartsANewOne() {
        // Seven planets already locked in; the active planet is one session from completing.
        var p = GalaxyProgress(
            activePlanetType = PlanetType.TERRAN,
            stage = PlanetStage.MOON,
            progress = 0.75f,
            planetsInSystem = 7,
            currentSystemIndex = 0,
        )
        val outcome = GalaxyEngine.applySessionCompleted(p, now = day, random = Random(1))
        assertThat(outcome.planetCompleted).isTrue()
        assertThat(outcome.systemCompleted).isTrue()
        assertThat(outcome.progress.systemsCompleted).isEqualTo(1)
        assertThat(outcome.progress.currentSystemIndex).isEqualTo(1)
        assertThat(outcome.progress.planetsInSystem).isEqualTo(0)
        assertThat(outcome.progress.stage).isEqualTo(PlanetStage.PLANET)
    }

    @Test
    fun meteorDestroysARandomCompletedPlanet() {
        val p = GalaxyProgress(planetsInSystem = 3, currentSystemIndex = 0)
        val planets = listOf(
            CompletedPlanet(id = 1, systemIndex = 0, slot = 0, type = PlanetType.TERRAN),
            CompletedPlanet(id = 2, systemIndex = 0, slot = 1, type = PlanetType.DWARF),
            CompletedPlanet(id = 3, systemIndex = 0, slot = 2, type = PlanetType.ICE_GIANT),
        )
        val effect = GalaxyEngine.planMeteorStrike(p, planets, random = Random(42))
        assertThat(effect).isInstanceOf(GalaxyEngine.MeteorEffect.PlanetDestroyed::class.java)
        effect as GalaxyEngine.MeteorEffect.PlanetDestroyed
        assertThat(planets).contains(effect.doomed)
        // The planet is only *marked* doomed here; count is unchanged until impact resolves.
        assertThat(effect.progress.doomedPlanetId).isEqualTo(effect.doomed.id)
        assertThat(effect.progress.planetsInSystem).isEqualTo(3)
        assertThat(effect.progress.meteorStrikes).isEqualTo(1)
    }

    @Test
    fun resolveDoomedPlanet_dropsCountAndClearsMarker() {
        val p = GalaxyProgress(planetsInSystem = 3, doomedPlanetId = 2L)
        val resolved = GalaxyEngine.resolveDoomedPlanet(p)
        assertThat(resolved.planetsInSystem).isEqualTo(2)
        assertThat(resolved.doomedPlanetId).isNull()
    }

    @Test
    fun meteorWithNoPlanetsSetsBackActivePlanet() {
        val p = terran(stage = PlanetStage.MOON, progress = 0.2f)
        val effect = GalaxyEngine.planMeteorStrike(p, emptyList(), random = Random(0))
        assertThat(effect).isInstanceOf(GalaxyEngine.MeteorEffect.ActiveSetback::class.java)
        // 0.2 - 0.34 = -0.14 -> drops from MOON into PLANET.
        assertThat(effect.progress.stage).isEqualTo(PlanetStage.PLANET)
        assertThat(effect.progress.meteorStrikes).isEqualTo(1)
    }

    @Test
    fun meteorSetbackFloorsAtPlanetZero() {
        val effect = GalaxyEngine.planMeteorStrike(terran(), emptyList(), random = Random(0))
        assertThat(effect.progress.stage).isEqualTo(PlanetStage.PLANET)
        assertThat(effect.progress.progress).isEqualTo(0f)
    }

    @Test
    fun rollsAreWeightedTowardCommonTypes() {
        // Over many rolls the rare ROGUE should be far less frequent than TERRAN.
        val random = Random(7)
        var terranCount = 0
        var rogueCount = 0
        repeat(2_000) {
            // Complete a Terran to trigger a fresh roll each time.
            val outcome = GalaxyEngine.applySessionCompleted(
                terran(stage = PlanetStage.MOON, progress = 0.99f), now = day, random = random,
            )
            when (outcome.progress.activePlanetType) {
                PlanetType.TERRAN -> terranCount++
                PlanetType.ROGUE -> rogueCount++
                else -> Unit
            }
        }
        assertThat(terranCount).isGreaterThan(rogueCount)
    }

    @Test
    fun streak_incrementsOnConsecutiveDays() {
        var p = GalaxyEngine.applySessionCompleted(terran(), now = day * 10).progress
        assertThat(p.currentStreakDays).isEqualTo(1)
        p = GalaxyEngine.applySessionCompleted(p, now = day * 11).progress
        assertThat(p.currentStreakDays).isEqualTo(2)
        assertThat(p.longestStreakDays).isEqualTo(2)
    }

    @Test
    fun streak_unchangedSameDay() {
        var p = GalaxyEngine.applySessionCompleted(terran(), now = day * 10).progress
        p = GalaxyEngine.applySessionCompleted(p, now = day * 10 + 1000).progress
        assertThat(p.currentStreakDays).isEqualTo(1)
    }

    @Test
    fun streak_resetsAfterGap() {
        var p = GalaxyEngine.applySessionCompleted(terran(), now = day * 10).progress
        p = GalaxyEngine.applySessionCompleted(p, now = day * 11).progress
        assertThat(p.currentStreakDays).isEqualTo(2)
        p = GalaxyEngine.applySessionCompleted(p, now = day * 14).progress
        assertThat(p.currentStreakDays).isEqualTo(1)
        assertThat(p.longestStreakDays).isEqualTo(2)
    }

    @Test
    fun systemFraction_reflectsCompletedPlanets() {
        assertThat(GalaxyEngine.systemFraction(GalaxyProgress())).isEqualTo(0f)
        // 4 of 8 planets, active planet fresh -> ~0.5
        assertThat(GalaxyEngine.systemFraction(GalaxyProgress(planetsInSystem = 4)))
            .isWithin(1e-4f).of(0.5f)
        // All 8 (system about to complete) -> 1.0
        assertThat(GalaxyEngine.systemFraction(GalaxyProgress(planetsInSystem = 8)))
            .isEqualTo(1f)
    }
}

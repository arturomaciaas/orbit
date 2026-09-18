package com.orbit.blocker.domain.gamification

import com.orbit.blocker.data.model.CompletedPlanet
import com.orbit.blocker.data.model.GalaxyProgress
import com.orbit.blocker.data.model.PlanetStage
import com.orbit.blocker.data.model.PlanetType
import java.util.concurrent.TimeUnit
import kotlin.random.Random

/**
 * Pure gamification math for the space-themed progression, decoupled from Room and
 * Android so it is fully unit-testable.
 *
 * ## Model
 * The cosmos is three nested layers (see [GalaxyProgress]):
 *  - **Active planet** — grown by focus sessions through [PlanetStage.PLANET] ->
 *    [PlanetStage.MOON] -> (for ringed types) [PlanetStage.RINGS]. [GalaxyProgress.progress]
 *    is a 0f..1f value within the current stage; each completed session adds
 *    [GROWTH_PER_SESSION].
 *  - **Solar system** — when a planet reaches its type's final stage it locks in as a
 *    [CompletedPlanet]. At [GalaxyProgress.PLANETS_PER_SYSTEM] planets the system is
 *    complete: it becomes a star in the galaxy and a fresh system begins.
 *  - **Galaxy** — the count of completed solar systems.
 *
 * A meteor strike (wrong-answer or quick-access punishment) destroys a random completed
 * planet in the current system; if there are none, it knocks the active planet back.
 */
object GalaxyEngine {

    /** Progress gained per completed focus session. Each stage needs ~4 sessions. */
    const val GROWTH_PER_SESSION = 0.25f

    /** Progress lost by the active planet when a meteor strikes an empty system. */
    const val SETBACK_PER_STRIKE = 0.34f

    /** Ordered lifecycle stages a planet can pass through. */
    private val STAGES = PlanetStage.entries

    /**
     * Relative weights for rolling the next active planet type. The "crazy" [PlanetType.ROGUE]
     * is deliberately rare so it stays special; ringed giants sit in the middle.
     */
    private val TYPE_WEIGHTS: Map<PlanetType, Int> = mapOf(
        PlanetType.TERRAN to 5,
        PlanetType.DWARF to 5,
        PlanetType.RINGED_GIANT to 4,
        PlanetType.ICE_GIANT to 3,
        PlanetType.ROGUE to 1,
    )

    /**
     * Outcome of applying a completed focus session. [progress] is always the new state to
     * persist. When [planetCompleted] is true, [lockedPlanetType] finished its lifecycle and
     * should be recorded as a [CompletedPlanet] at [lockedSlot] in [lockedSystemIndex].
     * When [systemCompleted] is also true, that planet was the 8th and the whole system
     * ignited (a fresh empty system has already been rolled into [progress]).
     */
    data class SessionOutcome(
        val progress: GalaxyProgress,
        val planetCompleted: Boolean = false,
        val systemCompleted: Boolean = false,
        val lockedPlanetType: PlanetType? = null,
        val lockedSlot: Int = -1,
        val lockedSystemIndex: Int = -1,
    )

    /**
     * Applies growth for one completed focus session, updating the active planet's
     * stage/progress, session count, and streak. If the planet reaches its type's final
     * stage it is marked complete and a new active planet (of a freshly rolled type) begins.
     *
     * [now] drives streak logic; [random] drives the next-type roll — both injectable for tests.
     */
    fun applySessionCompleted(
        current: GalaxyProgress,
        now: Long = System.currentTimeMillis(),
        random: Random = Random.Default,
    ): SessionOutcome {
        val streak = nextStreak(current.currentStreakDays, current.lastSessionCompletedAt, now)
        val base = current.copy(
            totalSessionsCompleted = current.totalSessionsCompleted + 1,
            currentStreakDays = streak,
            longestStreakDays = maxOf(current.longestStreakDays, streak),
            lastSessionCompletedAt = now,
        )

        val finalStage = current.activePlanetType.finalStage
        val (stage, progress, completed) = grow(
            current.activePlanetType,
            current.stage,
            base.progress + GROWTH_PER_SESSION,
        )

        if (!completed) {
            return SessionOutcome(progress = base.copy(stage = stage, progress = progress))
        }

        // The active planet just finished. Lock it into the current system.
        val lockedType = current.activePlanetType
        val lockedSlot = current.planetsInSystem
        val lockedSystem = current.currentSystemIndex
        val planetsNow = current.planetsInSystem + 1
        val systemDone = planetsNow >= GalaxyProgress.PLANETS_PER_SYSTEM

        val next = if (systemDone) {
            // 8th planet: ignite the system, start a fresh empty one, count a galaxy star.
            base.copy(
                activePlanetType = rollType(random),
                stage = PlanetStage.PLANET,
                progress = 0f,
                planetsInSystem = 0,
                currentSystemIndex = current.currentSystemIndex + 1,
                systemsCompleted = current.systemsCompleted + 1,
            )
        } else {
            base.copy(
                activePlanetType = rollType(random),
                stage = PlanetStage.PLANET,
                progress = 0f,
                planetsInSystem = planetsNow,
            )
        }

        return SessionOutcome(
            progress = next,
            planetCompleted = true,
            systemCompleted = systemDone,
            lockedPlanetType = lockedType,
            lockedSlot = lockedSlot,
            lockedSystemIndex = lockedSystem,
        )
    }

    /**
     * Decides the effect of a meteor strike. [meteorStrikes] is always incremented.
     *
     * If the current system has (non-doomed) completed planets, one is chosen at random and
     * *marked* for destruction ([MeteorEffect.PlanetDestroyed]) by setting
     * [GalaxyProgress.doomedPlanetId]. The planet is NOT removed here — the UI flies a meteor
     * to it and calls [resolveDoomedPlanet] on impact to actually delete it. This keeps the
     * animation and the data change in sync and lets the destruction be shown even if the
     * app was closed when the strike fired.
     *
     * If there are no planets to destroy, the active planet is knocked back
     * ([MeteorEffect.ActiveSetback]) immediately.
     */
    fun planMeteorStrike(
        current: GalaxyProgress,
        systemPlanets: List<CompletedPlanet>,
        random: Random = Random.Default,
    ): MeteorEffect {
        val struck = current.copy(meteorStrikes = current.meteorStrikes + 1)
        // Only target planets that aren't already marked doomed by a prior un-animated strike.
        val targetable = systemPlanets.filter { it.id != current.doomedPlanetId }
        if (targetable.isEmpty()) {
            val (stage, progress) = regress(
                current.activePlanetType,
                current.stage,
                current.progress - SETBACK_PER_STRIKE,
            )
            return MeteorEffect.ActiveSetback(
                progress = struck.copy(stage = stage, progress = progress),
            )
        }
        val victim = targetable[random.nextInt(targetable.size)]
        return MeteorEffect.PlanetDestroyed(
            progress = struck.copy(doomedPlanetId = victim.id),
            doomed = victim,
        )
    }

    /**
     * Applies the actual removal of a doomed planet at meteor impact: decrements
     * [GalaxyProgress.planetsInSystem] and clears [GalaxyProgress.doomedPlanetId]. The caller
     * deletes the [CompletedPlanet] row separately.
     */
    fun resolveDoomedPlanet(current: GalaxyProgress): GalaxyProgress = current.copy(
        planetsInSystem = (current.planetsInSystem - 1).coerceAtLeast(0),
        doomedPlanetId = null,
    )

    /** The two possible results of a meteor strike. */
    sealed interface MeteorEffect {
        val progress: GalaxyProgress

        /** A completed planet was marked for destruction (removed later, at impact). */
        data class PlanetDestroyed(
            override val progress: GalaxyProgress,
            val doomed: CompletedPlanet,
        ) : MeteorEffect

        /** No completed planets to destroy; the active planet was set back instead. */
        data class ActiveSetback(
            override val progress: GalaxyProgress,
        ) : MeteorEffect
    }

    /**
     * Overall completion of the *current solar system* in 0f..1f, for a single progress ring.
     * Blends fully-completed planets with the active planet's own fractional progress.
     */
    fun systemFraction(progress: GalaxyProgress): Float {
        val perPlanet = 1f / GalaxyProgress.PLANETS_PER_SYSTEM
        val done = progress.planetsInSystem * perPlanet
        val active = planetLifecycleFraction(progress.activePlanetType, progress.stage, progress.progress) * perPlanet
        return (done + active).coerceIn(0f, 1f)
    }

    // region internal

    /**
     * Grows a planet of [type] from [stage]/[rawProgress]. Returns the new stage, the
     * progress within it, and whether the planet has passed its type's final stage
     * (i.e. is complete). Progress is clamped at the final stage until completion tips over.
     */
    private fun grow(
        type: PlanetType,
        stage: PlanetStage,
        rawProgress: Float,
    ): Triple<PlanetStage, Float, Boolean> {
        var index = stage.ordinal
        var progress = rawProgress
        val finalIndex = type.finalStage.ordinal
        while (progress >= 1f) {
            if (index >= finalIndex) {
                // Finished the final stage of this type -> planet complete.
                return Triple(type.finalStage, 1f, true)
            }
            progress -= 1f
            index++
        }
        return Triple(STAGES[index], progress.coerceIn(0f, 1f), false)
    }

    /** Regresses the active planet on setback; floors at PLANET 0.0. */
    private fun regress(
        type: PlanetType,
        stage: PlanetStage,
        rawProgress: Float,
    ): Pair<PlanetStage, Float> {
        var index = stage.ordinal
        var progress = rawProgress
        while (progress < 0f && index > 0) {
            index--
            progress += 1f
        }
        if (index == 0 && progress < 0f) progress = 0f
        return STAGES[index] to progress.coerceIn(0f, 1f)
    }

    /** Fraction (0f..1f) of a planet type's whole lifecycle represented by stage+progress. */
    private fun planetLifecycleFraction(type: PlanetType, stage: PlanetStage, progress: Float): Float {
        val stagesForType = type.finalStage.ordinal + 1 // PLANET(+MOON)(+RINGS)
        val done = stage.ordinal + progress.coerceIn(0f, 1f)
        return (done / stagesForType).coerceIn(0f, 1f)
    }

    /** Weighted-random pick of the next planet type. */
    private fun rollType(random: Random): PlanetType {
        val total = TYPE_WEIGHTS.values.sum()
        var roll = random.nextInt(total)
        for ((type, weight) in TYPE_WEIGHTS) {
            if (roll < weight) return type
            roll -= weight
        }
        return PlanetType.TERRAN // unreachable; weights are non-empty
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

package com.orbit.blocker.domain.gamification

import com.orbit.blocker.data.model.CompletedPlanet
import com.orbit.blocker.data.model.GalaxyProgress
import com.orbit.blocker.data.repository.GalaxyRepository
import kotlin.random.Random
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Applies gamification side effects to the persisted gamification state using the pure
 * [GalaxyEngine]. Kept `open` so tests can substitute a spy.
 */
@Singleton
open class GamificationEvents @Inject constructor(
    private val galaxyRepository: GalaxyRepository,
) {

    /**
     * Grows the active planet after a completed focus session. If the planet finishes its
     * lifecycle it is locked into the current solar system as a [CompletedPlanet]; if that
     * was the 8th planet the system ignites and a new one begins.
     */
    open suspend fun onFocusSessionCompleted(
        now: Long = System.currentTimeMillis(),
        random: Random = Random.Default,
    ) {
        val current = galaxyRepository.get()
        val outcome = GalaxyEngine.applySessionCompleted(current, now, random)
        if (outcome.planetCompleted && outcome.lockedPlanetType != null) {
            // Place the new planet in the lowest FREE orbit slot rather than blindly using the
            // planet count. After a meteor destroys a mid-list planet the count drops but the
            // survivors keep their slots, so `planetsInSystem` could collide with an occupied
            // slot (two planets sharing a slot -> one silently vanishes on render). Gap-filling
            // keeps every slot unique.
            val occupied = galaxyRepository.systemPlanets(outcome.lockedSystemIndex)
                .map { it.slot }
                .toSet()
            val freeSlot = (0 until GalaxyProgress.PLANETS_PER_SYSTEM).first { it !in occupied }
            galaxyRepository.addCompletedPlanet(
                CompletedPlanet(
                    systemIndex = outcome.lockedSystemIndex,
                    slot = freeSlot,
                    type = outcome.lockedPlanetType,
                    completedAt = now,
                )
            )
        }
        galaxyRepository.save(outcome.progress)
    }

    /**
     * A meteor strike: destroys a random completed planet in the current system (or knocks
     * the active planet back if the system is empty). Triggered by failing the quiz gate
     * (2 of 3 wrong) or by using the 1-minute quick-access bypass.
     */
    open suspend fun onMeteorStrike(random: Random = Random.Default) {
        val current = galaxyRepository.get()
        val systemPlanets = galaxyRepository.systemPlanets(current.currentSystemIndex)
        val effect = GalaxyEngine.planMeteorStrike(current, systemPlanets, random)
        // Note: a PlanetDestroyed effect only *marks* the planet (doomedPlanetId). The Cosmos
        // screen animates the meteor and calls [resolveMeteorImpact] to actually remove it.
        galaxyRepository.save(effect.progress)
    }

    /**
     * Finalizes a pending planet destruction at meteor impact: deletes the doomed planet row
     * and clears [com.orbit.blocker.data.model.GalaxyProgress.doomedPlanetId]. Safe to call
     * when nothing is doomed (no-op). Called by the UI once the impact animation lands.
     */
    open suspend fun resolveMeteorImpact() {
        val current = galaxyRepository.get()
        val doomedId = current.doomedPlanetId ?: return
        galaxyRepository.systemPlanets(current.currentSystemIndex)
            .firstOrNull { it.id == doomedId }
            ?.let { galaxyRepository.removeCompletedPlanet(it) }
        galaxyRepository.save(GalaxyEngine.resolveDoomedPlanet(current))
    }
}

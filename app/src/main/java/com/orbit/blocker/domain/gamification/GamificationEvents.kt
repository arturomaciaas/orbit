package com.orbit.blocker.domain.gamification

import com.orbit.blocker.data.repository.GalaxyRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Applies gamification side effects to the persisted [com.orbit.blocker.data.model.GalaxyProgress]
 * using the pure [GalaxyEngine]. Kept `open` so tests can substitute a spy.
 */
@Singleton
open class GamificationEvents @Inject constructor(
    private val galaxyRepository: GalaxyRepository,
) {

    /** Grows the galaxy after a completed focus session. */
    open suspend fun onFocusSessionCompleted(now: Long = System.currentTimeMillis()) {
        val current = galaxyRepository.get()
        galaxyRepository.save(GalaxyEngine.applySessionCompleted(current, now))
    }

    /** Applies a meteor setback when the user misses gate questions. */
    open suspend fun onWrongAnswer(wrongCount: Int) {
        if (wrongCount <= 0) return
        val current = galaxyRepository.get()
        galaxyRepository.save(GalaxyEngine.applyMeteorStrike(current, wrongCount))
    }
}

package com.orbit.blocker.ui.home

import com.orbit.blocker.data.model.GalaxyStage

/**
 * Pure mapping from a [GalaxyStage] to the visual features the Canvas should draw.
 * Kept free of Compose types so it is unit-testable and easy to tune.
 *
 * The cosmos builds up stage by stage:
 *  - PLANET: a single planet.
 *  - MOON:   planet + one orbiting moon.
 *  - RINGS:  planet + moon + a ring around the planet.
 *  - SYSTEM: a central star with multiple orbiting planets (+ rings).
 *  - GALAXY: a dense, many-bodied system with a bright core and spiral haze.
 */
data class GalaxyVisual(
    val orbitingBodies: Int,
    val hasRings: Boolean,
    val hasCentralStar: Boolean,
    val starDensity: Int,
    val hasSpiralHaze: Boolean,
    val title: String,
) {
    companion object {
        fun forStage(stage: GalaxyStage): GalaxyVisual = when (stage) {
            GalaxyStage.PLANET -> GalaxyVisual(
                orbitingBodies = 0, hasRings = false, hasCentralStar = false,
                starDensity = 40, hasSpiralHaze = false, title = "Planet",
            )
            GalaxyStage.MOON -> GalaxyVisual(
                orbitingBodies = 1, hasRings = false, hasCentralStar = false,
                starDensity = 60, hasSpiralHaze = false, title = "Moon",
            )
            GalaxyStage.RINGS -> GalaxyVisual(
                orbitingBodies = 1, hasRings = true, hasCentralStar = false,
                starDensity = 80, hasSpiralHaze = false, title = "Rings",
            )
            GalaxyStage.SYSTEM -> GalaxyVisual(
                orbitingBodies = 4, hasRings = true, hasCentralStar = true,
                starDensity = 110, hasSpiralHaze = false, title = "System",
            )
            GalaxyStage.GALAXY -> GalaxyVisual(
                orbitingBodies = 7, hasRings = true, hasCentralStar = true,
                starDensity = 160, hasSpiralHaze = true, title = "Galaxy",
            )
        }
    }
}

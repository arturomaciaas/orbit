package com.orbit.blocker.ui.home

import com.google.common.truth.Truth.assertThat
import com.orbit.blocker.data.model.GalaxyStage
import org.junit.Test

class GalaxyVisualTest {

    @Test
    fun eachStageHasADistinctTitle() {
        val titles = GalaxyStage.entries.map { GalaxyVisual.forStage(it).title }
        assertThat(titles).containsNoDuplicates()
        assertThat(titles).hasSize(GalaxyStage.entries.size)
    }

    @Test
    fun complexityIncreasesWithStage() {
        val planet = GalaxyVisual.forStage(GalaxyStage.PLANET)
        val galaxy = GalaxyVisual.forStage(GalaxyStage.GALAXY)

        assertThat(planet.orbitingBodies).isEqualTo(0)
        assertThat(planet.hasRings).isFalse()
        assertThat(planet.hasCentralStar).isFalse()

        assertThat(galaxy.orbitingBodies).isGreaterThan(planet.orbitingBodies)
        assertThat(galaxy.starDensity).isGreaterThan(planet.starDensity)
        assertThat(galaxy.hasSpiralHaze).isTrue()
        assertThat(galaxy.hasCentralStar).isTrue()
    }

    @Test
    fun ringsAppearFromRingsStageOnward() {
        assertThat(GalaxyVisual.forStage(GalaxyStage.MOON).hasRings).isFalse()
        assertThat(GalaxyVisual.forStage(GalaxyStage.RINGS).hasRings).isTrue()
        assertThat(GalaxyVisual.forStage(GalaxyStage.SYSTEM).hasRings).isTrue()
        assertThat(GalaxyVisual.forStage(GalaxyStage.GALAXY).hasRings).isTrue()
    }

    @Test
    fun starDensityMonotonicNonDecreasing() {
        val densities = GalaxyStage.entries.map { GalaxyVisual.forStage(it).starDensity }
        densities.zipWithNext { a, b -> assertThat(b).isAtLeast(a) }
    }
}

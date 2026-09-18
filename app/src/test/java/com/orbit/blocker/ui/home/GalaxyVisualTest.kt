package com.orbit.blocker.ui.home

import com.google.common.truth.Truth.assertThat
import com.orbit.blocker.data.model.PlanetStage
import com.orbit.blocker.data.model.PlanetType
import org.junit.Test

class GalaxyVisualTest {

    @Test
    fun eachTypeHasADistinctTitle() {
        val titles = PlanetType.entries.map { PlanetVisual.forType(it).title }
        assertThat(titles).containsNoDuplicates()
        assertThat(titles).hasSize(PlanetType.entries.size)
    }

    @Test
    fun ringedTypesHaveRingsNonRingedDoNot() {
        assertThat(PlanetVisual.forType(PlanetType.TERRAN).hasRings).isFalse()
        assertThat(PlanetVisual.forType(PlanetType.DWARF).hasRings).isFalse()
        assertThat(PlanetVisual.forType(PlanetType.RINGED_GIANT).hasRings).isTrue()
        assertThat(PlanetVisual.forType(PlanetType.ICE_GIANT).hasRings).isTrue()
        assertThat(PlanetVisual.forType(PlanetType.ROGUE).hasRings).isTrue()
    }

    @Test
    fun typesDifferInMoreThanColour() {
        // Sizes vary across types (not just colours).
        val radii = PlanetType.entries.map { PlanetVisual.forType(it).radiusFraction }.toSet()
        assertThat(radii.size).isGreaterThan(1)
    }

    @Test
    fun onlyRogueIsErratic() {
        PlanetType.entries.forEach { type ->
            assertThat(PlanetVisual.forType(type).erratic).isEqualTo(type == PlanetType.ROGUE)
        }
    }

    @Test
    fun moonsAppearFromMoonStage() {
        val terran = PlanetVisual.forType(PlanetType.TERRAN)
        assertThat(terran.visibleMoons(PlanetStage.PLANET)).isEqualTo(0)
        assertThat(terran.visibleMoons(PlanetStage.MOON)).isEqualTo(terran.moonCount)
    }

    @Test
    fun ringsOnlyShowAtRingsStage() {
        val giant = PlanetVisual.forType(PlanetType.RINGED_GIANT)
        assertThat(giant.showRings(PlanetStage.PLANET)).isFalse()
        assertThat(giant.showRings(PlanetStage.MOON)).isFalse()
        assertThat(giant.showRings(PlanetStage.RINGS)).isTrue()

        // A non-ringed type never shows rings.
        val terran = PlanetVisual.forType(PlanetType.TERRAN)
        assertThat(terran.showRings(PlanetStage.RINGS)).isFalse()
    }
}

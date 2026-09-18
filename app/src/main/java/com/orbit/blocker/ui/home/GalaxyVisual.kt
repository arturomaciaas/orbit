package com.orbit.blocker.ui.home

import androidx.compose.ui.graphics.Color
import com.orbit.blocker.data.model.PlanetStage
import com.orbit.blocker.data.model.PlanetType
import com.orbit.blocker.ui.theme.CometCyan
import com.orbit.blocker.ui.theme.NebulaBlue
import com.orbit.blocker.ui.theme.NebulaPink
import com.orbit.blocker.ui.theme.NebulaTeal
import com.orbit.blocker.ui.theme.NebulaViolet
import com.orbit.blocker.ui.theme.SolarGold
import com.orbit.blocker.ui.theme.SolarOrange
import com.orbit.blocker.ui.theme.StarWhite

/**
 * Pure description of how a [PlanetType] should be drawn. Kept free of Compose drawing
 * types (only [Color]) so it is unit-testable and easy to tune. Each type differs on
 * more than colour — size, ring geometry, moon count, surface treatment, and one
 * deliberately erratic outlier ([PlanetType.ROGUE]).
 *
 * [PlanetVisual] describes the *finished* planet; the active-planet close-up additionally
 * gates the moon/rings by the current [PlanetStage] via [visibleMoons]/[showRings].
 */
data class PlanetVisual(
    val type: PlanetType,
    val title: String,
    /** Base disc radius as a fraction of the min canvas dimension. */
    val radiusFraction: Float,
    /** Day-side sphere gradient (light -> dark). */
    val surface: List<Color>,
    /** Accent used for surface bands/spots/storms. */
    val accent: Color,
    /** Number of moons this type carries when fully grown. */
    val moonCount: Int,
    val hasRings: Boolean,
    /** Ring band colours (empty when [hasRings] is false). */
    val ringColors: List<Color>,
    /** Ring radii as fractions of the min dimension: horizontal, vertical (for the ellipse). */
    val ringRx: Float,
    val ringRy: Float,
    /** Base ring tilt in degrees (the outlier tilts hard). */
    val ringTilt: Float,
    /** True for the "crazy" world: irregular wobble, elongated ring, storm colours. */
    val erratic: Boolean,
) {
    companion object {
        fun forType(type: PlanetType): PlanetVisual = when (type) {
            PlanetType.TERRAN -> PlanetVisual(
                type = type,
                title = "Terran World",
                radiusFraction = 0.17f,
                surface = listOf(NebulaTeal, NebulaBlue, NebulaViolet),
                accent = CometCyan,
                moonCount = 1,
                hasRings = false,
                ringColors = emptyList(),
                ringRx = 0f, ringRy = 0f, ringTilt = 0f,
                erratic = false,
            )
            PlanetType.DWARF -> PlanetVisual(
                type = type,
                title = "Dwarf World",
                radiusFraction = 0.11f,
                surface = listOf(StarWhite, NebulaBlue.copy(alpha = 0.7f), NebulaViolet),
                accent = NebulaViolet,
                moonCount = 1, // a tiny captured asteroid
                hasRings = false,
                ringColors = emptyList(),
                ringRx = 0f, ringRy = 0f, ringTilt = 0f,
                erratic = false,
            )
            PlanetType.RINGED_GIANT -> PlanetVisual(
                type = type,
                title = "Ringed Giant",
                radiusFraction = 0.22f,
                surface = listOf(SolarGold, SolarOrange, NebulaViolet),
                accent = SolarOrange,
                moonCount = 1,
                hasRings = true,
                ringColors = listOf(SolarGold.copy(alpha = 0.8f), SolarOrange.copy(alpha = 0.7f)),
                ringRx = 0.40f, ringRy = 0.13f, ringTilt = 16f,
                erratic = false,
            )
            PlanetType.ICE_GIANT -> PlanetVisual(
                type = type,
                title = "Ice Giant",
                radiusFraction = 0.155f,
                surface = listOf(StarWhite, CometCyan, NebulaBlue),
                accent = CometCyan,
                moonCount = 2,
                hasRings = true,
                ringColors = listOf(CometCyan.copy(alpha = 0.6f), StarWhite.copy(alpha = 0.5f)),
                ringRx = 0.30f, ringRy = 0.06f, ringTilt = 62f, // thin, steeply tilted
                erratic = false,
            )
            PlanetType.ROGUE -> PlanetVisual(
                type = type,
                title = "Rogue World",
                radiusFraction = 0.185f,
                surface = listOf(SolarGold, NebulaPink, NebulaViolet),
                accent = NebulaPink,
                moonCount = 0,
                hasRings = true,
                ringColors = listOf(NebulaPink.copy(alpha = 0.8f), SolarOrange.copy(alpha = 0.6f)),
                ringRx = 0.46f, ringRy = 0.10f, ringTilt = 34f, // elongated, off-axis
                erratic = true,
            )
        }
    }

    /** How many moons should be visible at [stage] (moons appear from the MOON stage on). */
    fun visibleMoons(stage: PlanetStage): Int =
        if (stage.ordinal >= PlanetStage.MOON.ordinal) moonCount else 0

    /** Whether rings should render at [stage] (rings appear only at the RINGS stage). */
    fun showRings(stage: PlanetStage): Boolean =
        hasRings && stage.ordinal >= PlanetStage.RINGS.ordinal
}

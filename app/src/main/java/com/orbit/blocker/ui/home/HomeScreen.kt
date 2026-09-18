package com.orbit.blocker.ui.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.orbit.blocker.data.model.CosmosView
import com.orbit.blocker.data.model.GalaxyProgress
import com.orbit.blocker.data.model.PlanetStage
import com.orbit.blocker.domain.gamification.GalaxyEngine
import com.orbit.blocker.ui.components.GlassCard
import com.orbit.blocker.ui.components.GlassPill
import com.orbit.blocker.ui.components.GlassProgressBar
import com.orbit.blocker.ui.components.SectionLabel

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val progress by viewModel.progress.collectAsStateWithLifecycle()
    val systemPlanets by viewModel.systemPlanets.collectAsStateWithLifecycle()

    var view by remember { mutableStateOf(CosmosView.PLANET) }
    val planetVisual = remember(progress.activePlanetType) {
        PlanetVisual.forType(progress.activePlanetType)
    }

    // Meteor animation driver. A single Animatable feeds both the targeted (system view)
    // and the setback (planet view) impact renderers.
    val meteor = remember { Animatable(0f) }

    // The planet the meteor is currently striking, held locally for the full duration of the
    // animation. This is DECOUPLED from progress.doomedPlanetId on purpose: onMeteorImpact()
    // clears doomedPlanetId in the same save that deletes the planet row, and those two
    // updates reach the UI on independent flows. If the canvas keyed off doomedPlanetId, the
    // id could clear a frame before the deleted planet dropped out of systemPlanets — so the
    // struck planet would flicker back to life at its live orbit while a *different* (still
    // present) planet appeared to take the hit. Owning the id here keeps the target stable.
    var animatingDoomedId by remember { mutableStateOf<Long?>(null) }

    // A planet marked for destruction: fly a meteor at it in the SYSTEM view, then remove it
    // at impact. Keyed on the doomed id so a fresh strike restarts the sequence.
    LaunchedEffect(progress.doomedPlanetId) {
        val doomed = progress.doomedPlanetId
        if (doomed != null) {
            view = CosmosView.SOLAR_SYSTEM // make sure the user witnesses the nuke
            animatingDoomedId = doomed
            meteor.snapTo(0f)
            // Approach phase.
            meteor.animateTo(IMPACT_FRACTION, animationSpec = tween(750))
            // Impact: remove the planet now so the explosion replaces it.
            viewModel.onMeteorImpact()
            // Blast phase — keep targeting the same id so the explosion stays put and the
            // struck planet never reappears while systemPlanets catches up to the deletion.
            meteor.animateTo(1f, animationSpec = tween(650))
            meteor.snapTo(0f)
            animatingDoomedId = null
        }
    }

    // A pure setback (no planet to destroy): play the impact on the active planet close-up.
    // Detected as a strike-count increase that did NOT leave a doomed planet.
    var lastStrikes by remember { mutableStateOf(progress.meteorStrikes) }
    LaunchedEffect(progress.meteorStrikes) {
        val increased = progress.meteorStrikes > lastStrikes
        lastStrikes = progress.meteorStrikes
        if (increased && progress.doomedPlanetId == null) {
            view = CosmosView.PLANET
            meteor.snapTo(0f)
            meteor.animateTo(1f, animationSpec = tween(1100))
            meteor.snapTo(0f)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column {
            SectionLabel("Your cosmos")
            Text(
                cosmosTitle(view, progress, planetVisual.title),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                cosmosSubtitle(view, progress),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp),
            )
        }

        // Zoom-level switcher: Planet -> Solar System -> Galaxy.
        ViewSwitcher(view = view, onSelect = { view = it })

        // The animated scene sits directly on the space backdrop (no card) for max impact.
        // Scaled to 95% so the tab fits on screen without scrolling.
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .aspectRatio(1f),
            contentAlignment = Alignment.Center,
        ) {
            when (view) {
                CosmosView.PLANET -> PlanetCanvas(
                    type = progress.activePlanetType,
                    stage = progress.stage,
                    // Only show the close-up meteor for pure setbacks (no doomed planet).
                    meteorProgress = if (progress.doomedPlanetId == null) meteor.value else 0f,
                    modifier = Modifier.fillMaxSize(),
                )
                CosmosView.SOLAR_SYSTEM -> SolarSystemCanvas(
                    planets = systemPlanets,
                    doomedPlanetId = animatingDoomedId,
                    impactProgress = if (animatingDoomedId != null) meteor.value else 0f,
                    modifier = Modifier.fillMaxSize(),
                )
                CosmosView.GALAXY -> GalaxyCanvas(
                    systemsCompleted = progress.systemsCompleted,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        // Growth card — meaning depends on the current view.
        GrowthCard(view = view, progress = progress)

        // Stats card.
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
            ) {
                Stat(label = "Sessions", value = progress.totalSessionsCompleted.toString())
                Stat(label = "Planets", value = "${progress.planetsInSystem}/${GalaxyProgress.PLANETS_PER_SYSTEM}")
                Stat(label = "Systems", value = progress.systemsCompleted.toString())
                Stat(label = "Streak", value = "${progress.currentStreakDays}d")
            }
        }
    }
}

@Composable
private fun ViewSwitcher(view: CosmosView, onSelect: (CosmosView) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        GlassPill(text = "Planet", selected = view == CosmosView.PLANET, onClick = { onSelect(CosmosView.PLANET) })
        GlassPill(text = "System", selected = view == CosmosView.SOLAR_SYSTEM, onClick = { onSelect(CosmosView.SOLAR_SYSTEM) })
        GlassPill(text = "Galaxy", selected = view == CosmosView.GALAXY, onClick = { onSelect(CosmosView.GALAXY) })
    }
}

@Composable
private fun GrowthCard(view: CosmosView, progress: GalaxyProgress) {
    val (label, fraction, caption) = when (view) {
        CosmosView.PLANET -> Triple(
            "Planet growth",
            progress.progress,
            "${stageLabel(progress.stage)} — progress toward the next stage",
        )
        CosmosView.SOLAR_SYSTEM -> Triple(
            "System progress",
            GalaxyEngine.systemFraction(progress),
            "${progress.planetsInSystem} of ${GalaxyProgress.PLANETS_PER_SYSTEM} planets complete",
        )
        CosmosView.GALAXY -> Triple(
            "Galaxy",
            galaxyFraction(progress.systemsCompleted),
            "${progress.systemsCompleted} completed ${if (progress.systemsCompleted == 1) "system" else "systems"}",
        )
    }
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(label, style = MaterialTheme.typography.titleLarge)
            Text(
                "${(fraction * 100).toInt()}%",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Text(
            caption,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp),
        )
        GlassProgressBar(progress = fraction, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun Stat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            label.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

// region text helpers
private fun cosmosTitle(view: CosmosView, progress: GalaxyProgress, planetTitle: String): String =
    when (view) {
        CosmosView.PLANET -> planetTitle
        CosmosView.SOLAR_SYSTEM -> "Solar System ${progress.currentSystemIndex + 1}"
        CosmosView.GALAXY -> "Galaxy"
    }

private fun cosmosSubtitle(view: CosmosView, progress: GalaxyProgress): String =
    when (view) {
        CosmosView.PLANET -> "Growing now: ${stageLabel(progress.stage)}"
        CosmosView.SOLAR_SYSTEM -> "Complete 8 planets to ignite this system"
        CosmosView.GALAXY -> "Each completed system becomes a star"
    }

private fun stageLabel(stage: PlanetStage): String = when (stage) {
    PlanetStage.PLANET -> "Forming"
    PlanetStage.MOON -> "Capturing a moon"
    PlanetStage.RINGS -> "Forming rings"
}

/** A soft, unbounded sense of galaxy fullness for the progress bar (visual only). */
private fun galaxyFraction(systemsCompleted: Int): Float =
    (systemsCompleted / 8f).coerceIn(0f, 1f)
// endregion

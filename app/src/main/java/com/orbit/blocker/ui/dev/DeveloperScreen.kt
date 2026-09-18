package com.orbit.blocker.ui.dev

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.orbit.blocker.data.model.CompletedPlanet
import com.orbit.blocker.data.model.CosmosView
import com.orbit.blocker.data.model.GalaxyProgress
import com.orbit.blocker.data.model.PlanetStage
import com.orbit.blocker.data.model.PlanetType
import com.orbit.blocker.ui.components.GlassCard
import com.orbit.blocker.ui.components.GlassPill
import com.orbit.blocker.ui.components.GlassProgressBar
import com.orbit.blocker.ui.components.SectionLabel
import com.orbit.blocker.ui.home.GalaxyCanvas
import com.orbit.blocker.ui.home.PlanetCanvas
import com.orbit.blocker.ui.home.PlanetVisual
import com.orbit.blocker.ui.home.SolarSystemCanvas
import kotlinx.coroutines.launch

/**
 * Developer-only preview for the cosmos visuals. Lets you jump between zoom views, pick a
 * planet type, scrub its lifecycle stage, populate a mock solar system, and fire a meteor
 * strike using purely local mock state — nothing here touches the database.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeveloperScreen(onBack: () -> Unit) {
    var cosmosView by remember { mutableStateOf(CosmosView.PLANET) }
    var planetType by remember { mutableStateOf(PlanetType.RINGED_GIANT) }
    var stageIndex by remember { mutableIntStateOf(PlanetStage.RINGS.ordinal) }
    var progress by remember { mutableFloatStateOf(0.5f) }
    var systemPlanetCount by remember { mutableIntStateOf(5) }
    var systemsCompleted by remember { mutableIntStateOf(3) }

    val stage = PlanetStage.entries[stageIndex.coerceAtMost(planetType.finalStage.ordinal)]
    val visual = remember(planetType) { PlanetVisual.forType(planetType) }

    // Mock completed-planet list for the solar-system preview (assorted types).
    val mockPlanets = remember(systemPlanetCount) {
        val types = PlanetType.entries
        (0 until systemPlanetCount).map { slot ->
            CompletedPlanet(id = slot.toLong(), systemIndex = 0, slot = slot, type = types[slot % types.size])
        }
    }

    val meteor = remember { Animatable(0f) }
    var doomedId by remember { mutableStateOf<Long?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text("Developer", style = MaterialTheme.typography.headlineLarge)
        }
        Text(
            "Preview the cosmos with mock data. Nothing here is saved.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Box(
            modifier = Modifier.fillMaxWidth().aspectRatio(1f),
            contentAlignment = Alignment.Center,
        ) {
            when (cosmosView) {
                CosmosView.PLANET -> PlanetCanvas(
                    type = planetType,
                    stage = stage,
                    meteorProgress = if (doomedId == null) meteor.value else 0f,
                    modifier = Modifier.fillMaxSize(),
                )
                CosmosView.SOLAR_SYSTEM -> SolarSystemCanvas(
                    planets = mockPlanets,
                    doomedPlanetId = doomedId,
                    impactProgress = if (doomedId != null) meteor.value else 0f,
                    modifier = Modifier.fillMaxSize(),
                )
                CosmosView.GALAXY -> GalaxyCanvas(
                    systemsCompleted = systemsCompleted,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            SectionLabel("View")
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CosmosView.entries.forEach { v ->
                    GlassPill(
                        text = v.name.lowercase().replaceFirstChar { it.uppercase() }.replace("_", " "),
                        selected = v == cosmosView,
                        onClick = { cosmosView = v },
                    )
                }
            }
        }

        if (cosmosView == CosmosView.PLANET) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                SectionLabel("Planet type — ${visual.title}")
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    PlanetType.entries.forEach { t ->
                        GlassPill(
                            text = t.name.take(4),
                            selected = t == planetType,
                            onClick = { planetType = t },
                        )
                    }
                }
            }

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                SectionLabel("Lifecycle stage — ${stage.name}")
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    PlanetStage.entries.forEach { s ->
                        val allowed = s.ordinal <= planetType.finalStage.ordinal
                        GlassPill(
                            text = s.name.lowercase().replaceFirstChar { it.uppercase() },
                            selected = s == stage,
                            onClick = { if (allowed) stageIndex = s.ordinal },
                        )
                    }
                }
                Slider(
                    value = progress,
                    onValueChange = { progress = it },
                    valueRange = 0f..1f,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                )
                GlassProgressBar(progress = progress, modifier = Modifier.fillMaxWidth())
            }
        }

        if (cosmosView == CosmosView.SOLAR_SYSTEM) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        "Planets: $systemPlanetCount/${GalaxyProgress.PLANETS_PER_SYSTEM}",
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        GlassPill(text = "-", selected = false, onClick = { if (systemPlanetCount > 0) systemPlanetCount-- })
                        GlassPill(text = "+", selected = false, onClick = {
                            if (systemPlanetCount < GalaxyProgress.PLANETS_PER_SYSTEM) systemPlanetCount++
                        })
                    }
                }
            }
        }

        if (cosmosView == CosmosView.GALAXY) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("Completed systems: $systemsCompleted", color = MaterialTheme.colorScheme.onSurface)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        GlassPill(text = "-", selected = false, onClick = { if (systemsCompleted > 0) systemsCompleted-- })
                        GlassPill(text = "+", selected = false, onClick = { systemsCompleted++ })
                    }
                }
            }
        }

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            SectionLabel("Meteor strike")
            Text(
                "Fire the setback animation used when a planet is destroyed.",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
            )
            GlassPill(
                text = "Trigger meteor",
                selected = false,
                onClick = {
                    scope.launch {
                        if (cosmosView == CosmosView.SOLAR_SYSTEM && mockPlanets.isNotEmpty()) {
                            // Target a random mock planet, fly in, explode, then drop it.
                            doomedId = mockPlanets.random().id
                            meteor.snapTo(0f)
                            meteor.animateTo(com.orbit.blocker.ui.home.IMPACT_FRACTION, tween(750))
                            if (systemPlanetCount > 0) systemPlanetCount--
                            meteor.animateTo(1f, tween(650))
                            meteor.snapTo(0f)
                            doomedId = null
                        } else {
                            meteor.snapTo(0f)
                            meteor.animateTo(1f, animationSpec = tween(1100))
                            meteor.snapTo(0f)
                        }
                    }
                },
            )
        }
    }
}

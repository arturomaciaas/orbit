package com.orbit.blocker.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbit.blocker.data.model.CompletedPlanet
import com.orbit.blocker.data.model.GalaxyProgress
import com.orbit.blocker.data.repository.GalaxyRepository
import com.orbit.blocker.domain.gamification.GamificationEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val galaxyRepository: GalaxyRepository,
    private val gamificationEvents: GamificationEvents,
) : ViewModel() {

    val progress: StateFlow<GalaxyProgress> = galaxyRepository.observe()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = GalaxyProgress(),
        )

    /** Completed planets in the solar system currently being built. */
    val systemPlanets: StateFlow<List<CompletedPlanet>> = galaxyRepository.observe()
        .map { it.currentSystemIndex }
        .distinctUntilChanged()
        .flatMapLatest { galaxyRepository.observeSystemPlanets(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    /**
     * Called by the Cosmos screen when the meteor impact animation lands. Deletes the doomed
     * planet and clears the pending-destruction marker.
     */
    fun onMeteorImpact() {
        viewModelScope.launch { gamificationEvents.resolveMeteorImpact() }
    }
}

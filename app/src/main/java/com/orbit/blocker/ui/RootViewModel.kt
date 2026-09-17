package com.orbit.blocker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbit.blocker.data.settings.OrbitSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/** Decides whether to show onboarding or the main app on launch. */
@HiltViewModel
class RootViewModel @Inject constructor(
    settings: OrbitSettings,
) : ViewModel() {

    /** null = still loading; true/false = onboarding completed or not. */
    val onboardingComplete: StateFlow<Boolean?> = settings.onboardingComplete
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )
}

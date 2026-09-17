package com.orbit.blocker.ui.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbit.blocker.data.model.BlockedApp
import com.orbit.blocker.data.repository.BlockRepository
import com.orbit.blocker.domain.focus.FocusSessionManager
import com.orbit.blocker.domain.focus.FocusSessionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class FocusUiState(
    val managedApps: List<BlockedApp> = emptyList(),
    val session: FocusSessionState = FocusSessionState.INACTIVE,
)

@HiltViewModel
class FocusViewModel @Inject constructor(
    blockRepository: BlockRepository,
    private val focusSessionManager: FocusSessionManager,
) : ViewModel() {

    val uiState: StateFlow<FocusUiState> = combine(
        blockRepository.observeManagedApps(),
        focusSessionManager.state,
    ) { apps, session ->
        FocusUiState(managedApps = apps, session = session)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FocusUiState(),
    )
}

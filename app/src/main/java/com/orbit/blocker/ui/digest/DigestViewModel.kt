package com.orbit.blocker.ui.digest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbit.blocker.data.model.NotificationRecord
import com.orbit.blocker.data.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DigestUiState(
    val records: List<NotificationRecord> = emptyList(),
    val unreadCount: Int = 0,
)

@HiltViewModel
class DigestViewModel @Inject constructor(
    private val repository: NotificationRepository,
) : ViewModel() {

    val uiState: StateFlow<DigestUiState> = repository.observeAll()
        .map { records -> DigestUiState(records = records, unreadCount = records.count { !it.read }) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DigestUiState(),
        )

    fun markAllRead() {
        viewModelScope.launch { repository.markAllRead() }
    }

    fun clearAll() {
        viewModelScope.launch { repository.clearAll() }
    }
}

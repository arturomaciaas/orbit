package com.orbit.blocker.ui.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbit.blocker.data.backup.BackupManager
import com.orbit.blocker.data.settings.OrbitSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Transient user-facing message after a backup export/import. */
sealed interface BackupStatus {
    data object Idle : BackupStatus
    data object Working : BackupStatus
    data class Success(val message: String) : BackupStatus
    data class Error(val message: String) : BackupStatus
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settings: OrbitSettings,
    private val backupManager: BackupManager,
) : ViewModel() {

    val questionsRequired: StateFlow<Int> = settings.questionsRequired.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = OrbitSettings.DEFAULT_QUESTIONS_REQUIRED,
    )

    private val _backupStatus = MutableStateFlow<BackupStatus>(BackupStatus.Idle)
    val backupStatus: StateFlow<BackupStatus> = _backupStatus.asStateFlow()

    fun setQuestionsRequired(value: Int) {
        viewModelScope.launch { settings.setQuestionsRequired(value) }
    }

    fun exportTo(uri: Uri) {
        viewModelScope.launch {
            _backupStatus.value = BackupStatus.Working
            _backupStatus.value = runCatching { backupManager.exportTo(uri) }
                .fold(
                    onSuccess = { BackupStatus.Success("Backup exported") },
                    onFailure = { BackupStatus.Error(it.message ?: "Export failed") },
                )
        }
    }

    fun importFrom(uri: Uri) {
        viewModelScope.launch {
            _backupStatus.value = BackupStatus.Working
            _backupStatus.value = runCatching { backupManager.importFrom(uri) }
                .fold(
                    onSuccess = { BackupStatus.Success("Backup restored") },
                    onFailure = { BackupStatus.Error(it.message ?: "Import failed") },
                )
        }
    }

    fun clearBackupStatus() {
        _backupStatus.value = BackupStatus.Idle
    }

    /** Resets the onboarding flag so the guided setup shows again on next launch/root recompose. */
    fun rerunOnboarding() {
        viewModelScope.launch { settings.setOnboardingComplete(false) }
    }
}

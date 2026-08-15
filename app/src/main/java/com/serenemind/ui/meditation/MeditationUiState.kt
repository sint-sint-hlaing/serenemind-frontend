package com.serenemind.ui.meditation

import com.serenemind.model.response.MeditationDashboardResponse

sealed interface MeditationUiState {
    object Idle : MeditationUiState
    object Loading : MeditationUiState
    data class Success(val data: MeditationDashboardResponse) : MeditationUiState
    data class Error(val message: String) : MeditationUiState
}

sealed interface DownloadUiState {
    object Idle : DownloadUiState
    object Downloading : DownloadUiState
    object Success : DownloadUiState
    data class Error(val message: String) : DownloadUiState
}

data class TimerUiState(
    val selectedMinutes: Int = 10,
    val isSaving: Boolean = false,
    val isRunning: Boolean = false,
    val remainingMillis: Long = 0L,
    val error: String? = null,
    val successMessage: String? = null
)

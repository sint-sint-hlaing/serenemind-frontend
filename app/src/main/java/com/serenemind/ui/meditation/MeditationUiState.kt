package com.serenemind.ui.meditation

import com.serenemind.model.response.MeditationDashboardResponse

sealed interface MeditationUiState {
    object Idle : MeditationUiState
    object Loading : MeditationUiState
    data class Success(val data: MeditationDashboardResponse) : MeditationUiState
    data class Error(val message: String) : MeditationUiState
}

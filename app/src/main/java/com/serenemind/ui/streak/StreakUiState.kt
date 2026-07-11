package com.serenemind.ui.streak

import com.serenemind.model.response.StreakResponse

sealed class StreakUiState {
    object Loading : StreakUiState()
    data class Success(val streak: StreakResponse) : StreakUiState()
    data class Error(val message: String) : StreakUiState()
}

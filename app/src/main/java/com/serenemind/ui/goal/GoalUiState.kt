package com.serenemind.ui.goal

import com.serenemind.model.response.GoalResponse
import com.serenemind.model.response.UserGoal

sealed interface GoalUiState {
    object Loading : GoalUiState
    data class Success(val goals: List<GoalResponse>) : GoalUiState
    data class Error(val message: String) : GoalUiState
}

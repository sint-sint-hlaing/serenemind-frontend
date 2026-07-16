package com.serenemind.ui.goal

import com.serenemind.model.response.UserGoal

sealed interface GoalUiState {
    object Loading : GoalUiState
    data class Success(val goals: List<UserGoal>) : GoalUiState
    data class Error(val message: String) : GoalUiState
}

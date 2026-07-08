package com.serenemind.ui.mood

interface MoodUiState {
    object Idle : MoodUiState
    object Loading : MoodUiState
    object Success : MoodUiState
    data class Error(val message: String) : MoodUiState
}
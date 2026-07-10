package com.serenemind.ui.profile

import com.serenemind.model.response.ReminderResponse

sealed class ReminderUiState {
    object Loading : ReminderUiState()
    data class Success(val reminders: List<ReminderResponse>) : ReminderUiState()
    data class Error(val message: String) : ReminderUiState()
}

sealed class AddReminderUiState {
    object Idle : AddReminderUiState()
    object Loading : AddReminderUiState()
    object Success : AddReminderUiState()
    data class Error(val message: String) : AddReminderUiState()
}

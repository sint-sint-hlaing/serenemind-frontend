package com.serenemind.ui.notification

import com.serenemind.model.response.NotificationResponse

sealed class NotificationUiState {
    object Loading : NotificationUiState()
    data class Success(val notifications: List<NotificationResponse>) : NotificationUiState()
    data class Error(val message: String) : NotificationUiState()
}

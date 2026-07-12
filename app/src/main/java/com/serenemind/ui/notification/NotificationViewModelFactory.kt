package com.serenemind.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.serenemind.repository.NotificationRepository

class NotificationViewModelFactory(
    private val notificationRepository: NotificationRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotificationViewModel(notificationRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

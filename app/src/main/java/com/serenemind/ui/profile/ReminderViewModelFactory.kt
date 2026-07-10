package com.serenemind.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.serenemind.repository.ReminderRepository

class ReminderViewModelFactory(
    private val reminderRepository: ReminderRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReminderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReminderViewModel(reminderRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

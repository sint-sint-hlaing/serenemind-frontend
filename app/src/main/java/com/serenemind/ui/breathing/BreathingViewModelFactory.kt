package com.serenemind.ui.breathing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.serenemind.repository.BreathingRepository

class BreathingViewModelFactory(
    private val breathingRepository: BreathingRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BreathingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BreathingViewModel(breathingRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

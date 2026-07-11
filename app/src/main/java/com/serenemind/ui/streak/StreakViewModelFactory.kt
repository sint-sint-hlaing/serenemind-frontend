package com.serenemind.ui.streak

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.serenemind.repository.StreakRepository

class StreakViewModelFactory(
    private val streakRepository: StreakRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StreakViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StreakViewModel(streakRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

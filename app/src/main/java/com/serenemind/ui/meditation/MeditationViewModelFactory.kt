package com.serenemind.ui.meditation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.serenemind.repository.MeditationRepository

class MeditationViewModelFactory(private val repository: MeditationRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MeditationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MeditationViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

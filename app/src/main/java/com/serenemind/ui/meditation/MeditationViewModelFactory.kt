package com.serenemind.ui.meditation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.serenemind.repository.MeditationRepository
import com.serenemind.util.MeditationAlarmScheduler

class MeditationViewModelFactory(
    private val repository: MeditationRepository,
    private val scheduler: MeditationAlarmScheduler
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MeditationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MeditationViewModel(repository, scheduler) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

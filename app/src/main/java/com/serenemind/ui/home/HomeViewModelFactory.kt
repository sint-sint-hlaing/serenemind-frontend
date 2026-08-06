package com.serenemind.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.serenemind.datastore.ThemeManager
import com.serenemind.repository.DashboardRepository
import com.serenemind.repository.MoodRepository

class HomeViewModelFactory(
    private val dashboardRepository: DashboardRepository,
    private val moodRepository: MoodRepository,
    private val themeManager: ThemeManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(dashboardRepository, moodRepository, themeManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

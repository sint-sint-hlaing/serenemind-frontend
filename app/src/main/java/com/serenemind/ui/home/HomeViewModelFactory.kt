package com.serenemind.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.serenemind.repository.DashboardRepository

class HomeViewModelFactory(
    private val dashboardRepository: DashboardRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(dashboardRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
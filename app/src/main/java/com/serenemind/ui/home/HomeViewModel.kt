// HomeViewModel.kt
package com.serenemind.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serenemind.datastore.ThemeManager
import com.serenemind.model.response.WeeklyMoodResponse
import com.serenemind.network.NetworkResult
import com.serenemind.repository.DashboardRepository
import com.serenemind.repository.MoodRepository
import com.serenemind.util.RefreshSignals
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(
    private val dashboardRepository: DashboardRepository,
    private val moodRepository: MoodRepository,
    private val themeManager: ThemeManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _weeklyMood = MutableStateFlow<List<WeeklyMoodResponse>>(emptyList())
    val weeklyMood = _weeklyMood.asStateFlow()

    init {
        fetchDashboardData()
        fetchWeeklyMood()
        observeRefreshSignals()
    }

    private fun observeRefreshSignals() {
        viewModelScope.launch {
            RefreshSignals.refreshDashboard.collectLatest {
                fetchDashboardData(isSilent = true)
                fetchWeeklyMood()
            }
        }
    }

    fun fetchDashboardData(isSilent: Boolean = false) {
        viewModelScope.launch {
            dashboardRepository.getDashboardData().collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        if (!isSilent) _uiState.value = HomeUiState.Loading
                    }
                    is NetworkResult.Success -> {
                        _uiState.value = HomeUiState.Success(result.data)
                    }
                    is NetworkResult.Error -> {
                        if (!isSilent) _uiState.value = HomeUiState.Error(result.message)
                    }
                }
            }
        }
    }

    fun fetchWeeklyMood() {
        viewModelScope.launch {
            moodRepository.getWeeklyMood().collect { result ->
                if (result is NetworkResult.Success) {
                    _weeklyMood.value = result.data
                }
            }
        }
    }
}


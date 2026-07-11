package com.serenemind.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serenemind.datastore.ThemeManager
import com.serenemind.repository.DashboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeViewModel(
    private val dashboardRepository: DashboardRepository,
    private val themeManager: ThemeManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        fetchDashboardData()
    }

    fun fetchDashboardData(isSilent: Boolean = false) {
        viewModelScope.launch {
            if (!isSilent) {
                _uiState.value = HomeUiState.Loading
            }
            dashboardRepository.getDashboardData()
                .catch { e ->
                    if (!isSilent) _uiState.value = HomeUiState.Error("Exception: ${e.message}")
                }
                .collect { response ->
                    if (response.isSuccessful && response.body() != null) {
                        _uiState.value = HomeUiState.Success(response.body()!!)
                    } else if (!isSilent) {
                        val errorDetail = response.errorBody()?.string() ?: "Unknown error"
                        _uiState.value = HomeUiState.Error("Error ${response.code()}: $errorDetail")
                    }
                }
        }
    }

    suspend fun shouldShowCelebration(currentStreak: Int, isNewBest: Boolean): Boolean {
        if (!isNewBest) return false
        val lastCelebrated = themeManager.lastCelebratedStreak.first()
        return currentStreak > lastCelebrated
    }

    fun markCelebrationShown(streak: Int) {
        viewModelScope.launch {
            themeManager.saveLastCelebratedStreak(streak)
        }
    }
}

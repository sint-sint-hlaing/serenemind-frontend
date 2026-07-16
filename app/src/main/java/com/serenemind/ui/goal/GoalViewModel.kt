package com.serenemind.ui.goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serenemind.model.response.UserGoal
import com.serenemind.repository.GoalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GoalViewModel(private val repository: GoalRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<GoalUiState>(GoalUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _selectedGoal = MutableStateFlow<UserGoal?>(null)
    val selectedGoal = _selectedGoal.asStateFlow()

    init {
        fetchGoals()
    }

    fun fetchGoals() {
        viewModelScope.launch {
            _uiState.value = GoalUiState.Loading
            try {
                val response = repository.getAllGoals()
                if (response.isSuccessful) {
                    _uiState.value = GoalUiState.Success(response.body() ?: emptyList())
                } else {
                    _uiState.value = GoalUiState.Error("Failed to fetch goals: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = GoalUiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun selectGoal(goal: UserGoal) {
        _selectedGoal.value = goal
    }

    fun incrementProgress(id: Long) {
        viewModelScope.launch {
            try {
                val response = repository.updateProgress(id)
                if (response.isSuccessful) {
                    val updatedGoal = response.body()
                    fetchGoals()
                    if (_selectedGoal.value?.id == id) {
                        _selectedGoal.value = updatedGoal
                    }
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}

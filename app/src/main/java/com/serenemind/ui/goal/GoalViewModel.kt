package com.serenemind.ui.goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serenemind.model.request.GoalRequest
import com.serenemind.model.response.UserGoal
import com.serenemind.network.NetworkResult
import com.serenemind.repository.GoalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GoalViewModel(private val repository: GoalRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<GoalUiState>(GoalUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _selectedGoal = MutableStateFlow<UserGoal?>(null)
    val selectedGoal = _selectedGoal.asStateFlow()

    private val _createGoalSuccess = MutableStateFlow(false)
    val createGoalSuccess = _createGoalSuccess.asStateFlow()

    init {
        fetchGoals()
    }

    fun fetchGoals() {
        viewModelScope.launch {
            repository.getAllGoals().collect { result ->
                when (result) {
                    is NetworkResult.Loading -> _uiState.value = GoalUiState.Loading
                    is NetworkResult.Success -> _uiState.value = GoalUiState.Success(result.data)
                    is NetworkResult.Error -> _uiState.value = GoalUiState.Error(result.message)
                }
            }
        }
    }

    fun selectGoal(goal: UserGoal) {
        _selectedGoal.value = goal
    }

    fun incrementProgress(id: Long) {
        viewModelScope.launch {
            repository.updateProgress(id).collect { result ->
                if (result is NetworkResult.Success) {
                    val updatedGoal = result.data
                    fetchGoals()
                    if (_selectedGoal.value?.id == id) {
                        _selectedGoal.value = updatedGoal
                    }
                }
            }
        }
    }

    fun createGoal(
        title: String, 
        description: String, 
        targetDays: Int
    ) {
        viewModelScope.launch {
            val request = GoalRequest(title, description, targetDays)
            repository.createGoal(request).collect { result ->
                if (result is NetworkResult.Success) {
                    _createGoalSuccess.value = true
                    fetchGoals()
                } else if (result is NetworkResult.Error) {
                    android.util.Log.e("GoalViewModel", "Failed to create goal: ${result.message}")
                }
            }
        }
    }

    fun resetCreateGoalSuccess() {
        _createGoalSuccess.value = false
    }
}

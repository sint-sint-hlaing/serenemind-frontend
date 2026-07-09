package com.serenemind.ui.goal

import androidx.lifecycle.ViewModel // Ensure this import is present
import androidx.lifecycle.viewModelScope
import com.serenemind.model.response.UserGoal
import com.serenemind.network.GoalApiService // Ensure your import is correct
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GoalViewModel(private val api: GoalApiService) : ViewModel() {

    private val _goals = MutableStateFlow<List<UserGoal>>(emptyList())
    val goals = _goals.asStateFlow()

    fun fetchGoals() {
        viewModelScope.launch {
            try {
                // Assuming your API returns a List<UserGoal>
                _goals.value = api.getAllGoals()
            } catch (e: Exception) {
                // Optional: Handle error state (e.g., _uiState.value = Error)
            }
        }
    }

    fun incrementProgress(id: Long) {
        viewModelScope.launch {
            try {
                api.updateProgress(id)
                fetchGoals() // Refresh list after successful update
            } catch (e: Exception) {
                // Handle network error
            }
        }
    }
}
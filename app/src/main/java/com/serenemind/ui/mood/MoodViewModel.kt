package com.serenemind.ui.mood

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serenemind.model.entity.enums.MoodType
import com.serenemind.model.request.MoodRequest
import com.serenemind.repository.MoodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MoodViewModel(private val repository: MoodRepository): ViewModel() {
    private val _uiState = MutableStateFlow<MoodUiState>(MoodUiState.Idle)
    val uiState = _uiState.asStateFlow()

    // MoodViewModel.kt
    fun saveMood(mood: MoodRequest, intensity: Int, note: String) {
        // 1. Validation Logic
        if (mood == null) {
            _uiState.value = MoodUiState.Error("Please select a mood first!")
            return
        }

        // 2. Network Call
        viewModelScope.launch {
            _uiState.value = MoodUiState.Loading
            try {
                val request = MoodRequest(mood as MoodType, intensity, note)
                val response = repository.saveMood(request)

                if (response.isSuccessful) {
                    _uiState.value = MoodUiState.Success
                } else {
                    _uiState.value = MoodUiState.Error("Server error: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = MoodUiState.Error("Network error: ${e.localizedMessage}")
            }
        }
    }
    // Add this line to define the StateFlow
    private val _summaryState = MutableStateFlow<Map<String, Double>>(emptyMap())
    val summaryState = _summaryState.asStateFlow()

    init {
        fetchMoodSummary()
    }

    fun fetchMoodSummary() {
        viewModelScope.launch {
            try {
                // Assuming your repository returns the Map directly or via Response
                val data = repository.getMoodSummary()
                _summaryState.value = data
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
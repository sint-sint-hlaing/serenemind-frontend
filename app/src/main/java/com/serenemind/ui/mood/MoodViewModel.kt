package com.serenemind.ui.mood

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serenemind.model.entity.enums.MoodType
import com.serenemind.model.request.MoodRequest
import com.serenemind.model.response.DailyMoodResponse
import com.serenemind.repository.MoodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MoodViewModel(private val repository: MoodRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<MoodUiState>(MoodUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _summaryState = MutableStateFlow<Map<String, Double>>(emptyMap())
    val summaryState = _summaryState.asStateFlow()

    private val _historyState = MutableStateFlow<List<DailyMoodResponse>>(emptyList())
    val historyState = _historyState.asStateFlow()

    private val _selectedDateMood = MutableStateFlow<DailyMoodResponse?>(null)
    val selectedDateMood = _selectedDateMood.asStateFlow()

    init {
        fetchMoodSummary()
        val calendar = Calendar.getInstance()
        fetchMoodHistory(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)
    }

    fun fetchMoodSummary() {
        viewModelScope.launch {
            try {
                val data = repository.getMoodSummary()
                _summaryState.value = data
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun fetchMoodHistory(year: Int, month: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getMoodHistory(year, month)
                if (response.isSuccessful) {
                    val history = response.body() ?: emptyList()
                    _historyState.value = history

                    // Set today as selected by default if exists
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val today = sdf.format(Date())
                    _selectedDateMood.value = history.find { it.date == today }
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun selectDateMood(date: String) {
        _selectedDateMood.value = _historyState.value.find { it.date == date }
    }

    fun saveMood(mood: MoodType, intensity: Int, note: String) {
        viewModelScope.launch {
            _uiState.value = MoodUiState.Loading
            try {
                val request = MoodRequest(mood, intensity, note)
                val response = repository.saveMood(request)

                if (response.isSuccessful) {
                    _uiState.value = MoodUiState.Success
                    // Refresh data
                    fetchMoodSummary()
                    val calendar = Calendar.getInstance()
                    fetchMoodHistory(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    _uiState.value = MoodUiState.Error("Server error ${response.code()}: $errorBody")
                }
            } catch (e: Exception) {
                _uiState.value = MoodUiState.Error("Network error: ${e.localizedMessage}")
            }
        }
    }

    fun reset() {
        _uiState.value = MoodUiState.Idle
    }
}

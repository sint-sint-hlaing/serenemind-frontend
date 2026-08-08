package com.serenemind.ui.mood

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serenemind.model.entity.enums.MoodType
import com.serenemind.model.request.MoodRequest
import com.serenemind.model.response.DailyMoodResponse
import com.serenemind.model.response.WeeklyMoodResponse
import com.serenemind.network.NetworkResult
import com.serenemind.repository.MoodRepository
import com.serenemind.util.RefreshSignals
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

    private val _weeklySummary = MutableStateFlow<WeeklyMoodResponse?>(null)
    val weeklySummary = _weeklySummary.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        fetchMoodSummary()
        fetchWeeklySummary()
        val calendar = Calendar.getInstance()
        fetchMoodHistory(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)
    }

    fun fetchMoodSummary() {
        viewModelScope.launch {
            repository.getMoodSummary().collect { result ->
                if (result is NetworkResult.Success) {
                    _summaryState.value = result.data
                }
            }
        }
    }

    fun fetchWeeklySummary() {
        viewModelScope.launch {
            repository.getWeeklySummary().collect { result ->
                if (result is NetworkResult.Success) {
                    _weeklySummary.value = result.data
                }
            }
        }
    }

    fun fetchMoodHistory(year: Int, month: Int) {
        viewModelScope.launch {
            repository.getMoodHistory(year, month).collect { result ->
                if (result is NetworkResult.Success) {
                    val history = result.data
                    _historyState.value = history

                    // Set today as selected by default if exists
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val today = sdf.format(Date())
                    _selectedDateMood.value = history.find { it.date == today }
                }
            }
        }
    }

    fun selectDateMood(date: String) {
        _selectedDateMood.value = _historyState.value.find { it.date == date }
    }

    fun saveMood(mood: MoodType, intensity: Int, note: String) {
        viewModelScope.launch {
            val request = MoodRequest(mood, intensity, note)
            repository.saveMood(request).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _uiState.value = MoodUiState.Loading
                    }
                    is NetworkResult.Success -> {
                        _uiState.value = MoodUiState.Success
                        refresh()
                        RefreshSignals.signalRefreshDashboard()
                    }
                    is NetworkResult.Error -> {
                        _uiState.value = MoodUiState.Error(result.message)
                    }
                }
            }
        }
    }

    fun reset() {
        _uiState.value = MoodUiState.Idle
    }
}

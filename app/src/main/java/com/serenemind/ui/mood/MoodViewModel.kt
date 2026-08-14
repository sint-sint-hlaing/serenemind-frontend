// MoodViewModel.kt
package com.serenemind.ui.mood

import android.os.Build
import androidx.annotation.RequiresApi
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
import java.time.LocalDate
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            refresh()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchMoodHistory(year: Int, month: Int) {
        viewModelScope.launch {
            repository.getMoodHistory(year, month).collect { result ->
                if (result is NetworkResult.Success) {
                    val history = result.data
                    _historyState.value = history

                    // Set today as selected by default if exists
                    val today = LocalDate.now()
                    _selectedDateMood.value = history.find {
                        it.date == today
                    }
                }
            }
        }
    }

    fun selectDateMood(date: LocalDate) {
        _selectedDateMood.value = _historyState.value.find { it.date == date }
    }

    fun saveMood(mood: MoodType, intensity: Int, note: String) {
        viewModelScope.launch {
            val request = MoodRequest(
                mood = mood.name,
                intensity = intensity,
                note = note
            )
            _uiState.value = MoodUiState.Loading
            repository.saveMood(request).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _uiState.value = MoodUiState.Loading
                    }

                    is NetworkResult.Success -> {
                        _uiState.value = MoodUiState.Success
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            refresh()
                        }
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

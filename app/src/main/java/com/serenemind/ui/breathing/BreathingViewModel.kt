package com.serenemind.ui.breathing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serenemind.model.request.BreathingRequest
import com.serenemind.model.response.BreathingStartResponse
import com.serenemind.model.response.BreathingSummaryResponse
import com.serenemind.repository.BreathingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class BreathingUiState {
    object Idle : BreathingUiState()
    object Loading : BreathingUiState()
    data class SessionStarted(val response: BreathingStartResponse) : BreathingUiState()
    data class SessionSummary(val summary: BreathingSummaryResponse) : BreathingUiState()
    data class Error(val message: String) : BreathingUiState()
}

class BreathingViewModel(
    private val breathingRepository: BreathingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BreathingUiState>(BreathingUiState.Idle)
    val uiState: StateFlow<BreathingUiState> = _uiState.asStateFlow()

    fun startSession(exerciseType: String, durationMinutes: Int) {
        viewModelScope.launch {
            _uiState.value = BreathingUiState.Loading
            try {
                val response = breathingRepository.startSession(BreathingRequest(exerciseType, durationMinutes))
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = BreathingUiState.SessionStarted(response.body()!!)
                } else {
                    _uiState.value = BreathingUiState.Error("Failed to start session")
                }
            } catch (e: Exception) {
                _uiState.value = BreathingUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun completeSession(sessionId: String) {
        viewModelScope.launch {
            _uiState.value = BreathingUiState.Loading
            try {
                val response = breathingRepository.completeSession(sessionId)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = BreathingUiState.SessionSummary(response.body()!!)
                } else {
                    _uiState.value = BreathingUiState.Error("Failed to complete session")
                }
            } catch (e: Exception) {
                _uiState.value = BreathingUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun trackRound(sessionId: String, roundNumber: Int) {
        viewModelScope.launch {
            try {
                breathingRepository.trackRoundComplete(sessionId, roundNumber)
            } catch (e: Exception) {
                // Silently fail for tracking
            }
        }
    }

    fun resetToIdle() {
        _uiState.value = BreathingUiState.Idle
    }
}

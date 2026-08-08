package com.serenemind.ui.journal

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.serenemind.model.response.JournalAnalysisResponse
import com.serenemind.network.NetworkResult
import com.serenemind.repository.JournalRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class JournalAnalysisViewModel(private val repository: JournalRepository) : ViewModel() {

    private val _analysis = MutableStateFlow<JournalAnalysisResponse?>(null)
    val analysis: StateFlow<JournalAnalysisResponse?> = _analysis

    private val _uiState = MutableStateFlow<JournalAnalysisUiState>(JournalAnalysisUiState.Idle)
    val uiState: StateFlow<JournalAnalysisUiState> = _uiState

    fun loadAnalysis(id: Int) {
        viewModelScope.launch {
            _uiState.value = JournalAnalysisUiState.Loading
            repository.getAnalysis(id).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        val body = result.data
                        if (body.emotion != null) {
                            _analysis.value = body
                            _uiState.value = JournalAnalysisUiState.Idle
                        } else {
                            triggerAndRetry(id)
                        }
                    }
                    is NetworkResult.Error -> {
                        if (result.code == 404) {
                            triggerAndRetry(id)
                        } else {
                            _uiState.value = JournalAnalysisUiState.Error(result.message)
                        }
                    }
                    is NetworkResult.Loading -> {
                        _uiState.value = JournalAnalysisUiState.Loading
                    }
                }
            }
        }
    }

    private suspend fun triggerAndRetry(id: Int) {
        repository.triggerAnalysis(id).collect { triggerResult ->
            when (triggerResult) {
                is NetworkResult.Success -> {
                    // Polling mechanism: Try up to 5 times with a delay
                    var retryCount = 0
                    val maxRetries = 5
                    val delayMs = 3000L
                    
                    while (retryCount < maxRetries) {
                        delay(delayMs)
                        var found = false
                        repository.getAnalysis(id).collect { pollResult ->
                            if (pollResult is NetworkResult.Success && pollResult.data.emotion != null) {
                                _analysis.value = pollResult.data
                                _uiState.value = JournalAnalysisUiState.Idle
                                found = true
                            }
                        }
                        if (found) return@collect
                        retryCount++
                    }
                    _uiState.value = JournalAnalysisUiState.Error("AI is still thinking. Please come back in a moment!")
                }
                is NetworkResult.Error -> {
                    _uiState.value = JournalAnalysisUiState.Error("Failed to start AI analysis: ${triggerResult.message}")
                }
                else -> {}
            }
        }
    }
}

sealed class JournalAnalysisUiState {
    object Idle : JournalAnalysisUiState()
    object Loading : JournalAnalysisUiState()
    data class Error(val message: String) : JournalAnalysisUiState()
}

class JournalAnalysisViewModelFactory(private val repository: JournalRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JournalAnalysisViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return JournalAnalysisViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

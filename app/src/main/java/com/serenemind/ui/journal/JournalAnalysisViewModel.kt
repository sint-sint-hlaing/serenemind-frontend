package com.serenemind.ui.journal

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.serenemind.model.response.JournalAnalysisResponse
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
            try {
                Log.d("JournalAnalysis", "Fetching analysis for journal ID: $id")
                val response = repository.getAnalysis(id)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.emotion != null) {
                        Log.d("JournalAnalysis", "Analysis found: $body")
                        _analysis.value = body
                        _uiState.value = JournalAnalysisUiState.Idle
                    } else {
                        // Analysis object exists but it's empty (maybe still processing)
                        Log.d("JournalAnalysis", "Analysis object is empty, triggering/retrying...")
                        triggerAndRetry(id)
                    }
                } else if (response.code() == 404) {
                    Log.d("JournalAnalysis", "Analysis not found (404), triggering...")
                    triggerAndRetry(id)
                } else {
                    val error = response.errorBody()?.string() ?: response.message()
                    Log.e("JournalAnalysis", "Error loading analysis: $error")
                    _uiState.value = JournalAnalysisUiState.Error("Failed to load analysis ($error)")
                }
            } catch (e: Exception) {
                Log.e("JournalAnalysis", "Exception loading analysis", e)
                _uiState.value = JournalAnalysisUiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    private suspend fun triggerAndRetry(id: Int) {
        try {
            val triggerResponse = repository.triggerAnalysis(id)
            if (triggerResponse.isSuccessful) {
                Log.d("JournalAnalysis", "Analysis triggered successfully. Starting poll...")
                
                // Polling mechanism: Try up to 3 times with a delay
                var retryCount = 0
                val maxRetries = 5
                val delayMs = 3000L // 3 seconds between retries
                
                while (retryCount < maxRetries) {
                    delay(delayMs)
                    Log.d("JournalAnalysis", "Polling analysis (Attempt ${retryCount + 1})...")
                    val pollResponse = repository.getAnalysis(id)
                    if (pollResponse.isSuccessful) {
                        val pollBody = pollResponse.body()
                        if (pollBody != null && pollBody.emotion != null) {
                            Log.d("JournalAnalysis", "Analysis retrieved successfully during poll!")
                            _analysis.value = pollBody
                            _uiState.value = JournalAnalysisUiState.Idle
                            return
                        }
                    }
                    retryCount++
                }
                _uiState.value = JournalAnalysisUiState.Error("AI is still thinking. Please come back in a moment!")
            } else {
                val error = triggerResponse.errorBody()?.string() ?: triggerResponse.message()
                Log.e("JournalAnalysis", "Trigger failed: $error")
                _uiState.value = JournalAnalysisUiState.Error("Failed to start AI analysis: $error")
            }
        } catch (e: Exception) {
            Log.e("JournalAnalysis", "Exception in triggerAndRetry", e)
            _uiState.value = JournalAnalysisUiState.Error("Failed to process analysis: ${e.localizedMessage}")
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

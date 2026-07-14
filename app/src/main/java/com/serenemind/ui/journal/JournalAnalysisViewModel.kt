package com.serenemind.ui.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.serenemind.model.response.JournalAnalysisResponse
import com.serenemind.repository.JournalRepository
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
                val response = repository.getAnalysis(id)
                if (response.isSuccessful) {
                    _analysis.value = response.body()
                    _uiState.value = JournalAnalysisUiState.Idle
                } else if (response.code() == 404) {
                    // Trigger analysis if not found
                    triggerAnalysis(id)
                } else {
                    _uiState.value = JournalAnalysisUiState.Error("Failed to load analysis")
                }
            } catch (e: Exception) {
                _uiState.value = JournalAnalysisUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun triggerAnalysis(id: Int) {
        viewModelScope.launch {
            _uiState.value = JournalAnalysisUiState.Loading
            try {
                val response = repository.triggerAnalysis(id)
                if (response.isSuccessful) {
                    // Wait a bit and then reload or the backend might handle it
                    // For now, try to get it again
                    val analysisResponse = repository.getAnalysis(id)
                    if (analysisResponse.isSuccessful) {
                        _analysis.value = analysisResponse.body()
                        _uiState.value = JournalAnalysisUiState.Idle
                    } else {
                        _uiState.value = JournalAnalysisUiState.Error("Analysis triggered but failed to retrieve")
                    }
                } else {
                    _uiState.value = JournalAnalysisUiState.Error("Failed to trigger analysis")
                }
            } catch (e: Exception) {
                _uiState.value = JournalAnalysisUiState.Error(e.message ?: "Unknown error")
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

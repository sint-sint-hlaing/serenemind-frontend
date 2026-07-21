package com.serenemind.ui.meditation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serenemind.model.response.MeditationResponse
import com.serenemind.repository.MeditationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MeditationViewModel(private val repository: MeditationRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<MeditationUiState>(MeditationUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _selectedMeditation = MutableStateFlow<MeditationResponse?>(null)
    val selectedMeditation = _selectedMeditation.asStateFlow()

    init {
        fetchMeditationDashboard()
    }

    fun fetchMeditationDashboard() {
        viewModelScope.launch {
            _uiState.value = MeditationUiState.Loading
            repository.getDashboard().collect { response ->
                if (response.isSuccessful) {
                    response.body()?.let {
                        _uiState.value = MeditationUiState.Success(it)
                    } ?: run {
                        _uiState.value = MeditationUiState.Error("Empty response body")
                    }
                } else {
                    _uiState.value = MeditationUiState.Error("Server error: ${response.code()}")
                }
            }
        }
    }

    fun selectMeditation(meditation: MeditationResponse) {
        _selectedMeditation.value = meditation
    }
}

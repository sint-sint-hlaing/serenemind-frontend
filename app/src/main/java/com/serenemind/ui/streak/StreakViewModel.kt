package com.serenemind.ui.streak

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serenemind.repository.StreakRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class StreakViewModel(
    private val streakRepository: StreakRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<StreakUiState>(StreakUiState.Loading)
    val uiState: StateFlow<StreakUiState> = _uiState.asStateFlow()

    init {
        fetchStreak()
    }

    fun fetchStreak(isSilent: Boolean = false) {
        viewModelScope.launch {
            if (!isSilent) {
                _uiState.value = StreakUiState.Loading
            }
            streakRepository.getStreak()
                .catch { e ->
                    if (!isSilent) _uiState.value = StreakUiState.Error(e.message ?: "Unknown error")
                }
                .collect { response ->
                    if (response.isSuccessful && response.body() != null) {
                        _uiState.value = StreakUiState.Success(response.body()!!)
                    } else if (!isSilent) {
                        _uiState.value = StreakUiState.Error("Failed to fetch streak")
                    }
                }
        }
    }

    fun useFreeze() {
        viewModelScope.launch {
            try {
                val response = streakRepository.useStreakFreeze()
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = StreakUiState.Success(response.body()!!)
                } else {
                    // Handle error
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}

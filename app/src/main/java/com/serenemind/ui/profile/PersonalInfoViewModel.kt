package com.serenemind.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serenemind.model.response.PersonalInfoResponse
import com.serenemind.network.NetworkResult
import com.serenemind.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PersonalInfoViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PersonalInfoUiState>(PersonalInfoUiState.Loading)
    val uiState: StateFlow<PersonalInfoUiState> = _uiState.asStateFlow()

    init {
        fetchPersonalInfo()
    }

    fun fetchPersonalInfo() {
        viewModelScope.launch {
            userRepository.getPersonalInfo().collect { result ->
                when (result) {
                    is NetworkResult.Loading -> _uiState.value = PersonalInfoUiState.Loading
                    is NetworkResult.Success -> _uiState.value = PersonalInfoUiState.Success(result.data)
                    is NetworkResult.Error -> _uiState.value = PersonalInfoUiState.Error(result.message)
                }
            }
        }
    }
}

sealed interface PersonalInfoUiState {
    object Loading : PersonalInfoUiState
    data class Success(val data: PersonalInfoResponse) : PersonalInfoUiState
    data class Error(val message: String) : PersonalInfoUiState
}

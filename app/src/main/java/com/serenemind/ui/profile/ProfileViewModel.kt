package com.serenemind.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serenemind.network.NetworkResult
import com.serenemind.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        fetchUserProfile()
    }

    fun fetchUserProfile() {
        viewModelScope.launch {
            userRepository.getUserProfile().collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _uiState.value = ProfileUiState.Loading
                    }
                    is NetworkResult.Success -> {
                        val user = result.data
                        _uiState.value = ProfileUiState.Success(user)
                        
                        // Fetch activity
                        userRepository.getUserActivity().collect { activityResult ->
                            if (activityResult is NetworkResult.Success) {
                                _uiState.value = ProfileUiState.Success(user, activityResult.data)
                            }
                        }
                    }
                    is NetworkResult.Error -> {
                        _uiState.value = ProfileUiState.Error(result.message)
                    }
                }
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            userRepository.logout()
            onSuccess()
        }
    }
}

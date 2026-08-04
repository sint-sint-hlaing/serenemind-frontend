package com.serenemind.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serenemind.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
            try {
                _uiState.value = ProfileUiState.Loading
                userRepository.getUserProfile().collect { profileResponse ->
                    if (profileResponse.isSuccessful && profileResponse.body() != null) {
                        val user = profileResponse.body()!!
                        _uiState.value = ProfileUiState.Success(user)
                        
                        // Fetch activity
                        userRepository.getUserActivity().collect { activityResponse ->
                            if (activityResponse.isSuccessful && activityResponse.body() != null) {
                                _uiState.value = ProfileUiState.Success(user, activityResponse.body())
                            }
                        }
                    } else {
                        _uiState.value = ProfileUiState.Error("Failed to load profile details.")
                    }
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error("An error occurred: ${e.message}")
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
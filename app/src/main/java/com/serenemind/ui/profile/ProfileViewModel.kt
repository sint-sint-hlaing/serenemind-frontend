package com.serenemind.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serenemind.model.request.UpdateProfileRequest
import com.serenemind.model.response.AvatarResponse
import com.serenemind.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class ProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _updateStatus = MutableStateFlow<UpdateStatus>(UpdateStatus.Idle)
    val updateStatus: StateFlow<UpdateStatus> = _updateStatus.asStateFlow()

    private val _avatars = MutableStateFlow<List<AvatarResponse>>(emptyList())
    val avatars: StateFlow<List<AvatarResponse>> = _avatars.asStateFlow()

    init {
        fetchUserProfile()
        fetchAvatars()
    }

    fun fetchUserProfile() {
        viewModelScope.launch {
            try {
                _uiState.value = ProfileUiState.Loading
                userRepository.getUserProfile().collect { response ->
                    if (response.isSuccessful && response.body() != null) {
                        _uiState.value = ProfileUiState.Success(response.body()!!)
                    } else {
                        _uiState.value = ProfileUiState.Error("Failed to load profile details.")
                    }
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error("An error occurred: ${e.message}")
            }
        }
    }

    private fun fetchAvatars() {
        viewModelScope.launch {
            userRepository.getAvatars().collect { response ->
                if (response.isSuccessful && response.body() != null) {
                    _avatars.value = response.body()!!
                }
            }
        }
    }

    fun updateUserProfile(fullname: String, email: String, bio: String, location: String) {
        viewModelScope.launch {
            _updateStatus.value = UpdateStatus.Loading
            val request = UpdateProfileRequest(fullname, email, bio, location)
            val response = userRepository.updateUserProfile(request)
            if (response.isSuccessful && response.body() != null) {
                _uiState.value = ProfileUiState.Success(response.body()!!)
                _updateStatus.value = UpdateStatus.Success
            } else {
                _updateStatus.value = UpdateStatus.Error("Failed to update profile.")
            }
        }
    }

    fun uploadProfileImage(image: MultipartBody.Part) {
        viewModelScope.launch {
            _updateStatus.value = UpdateStatus.Loading
            val response = userRepository.uploadProfileImage(image)
            if (response.isSuccessful) {
                fetchUserProfile() // Refresh to get new image URL
                _updateStatus.value = UpdateStatus.Success
            } else {
                _updateStatus.value = UpdateStatus.Error("Failed to upload image.")
            }
        }
    }

    fun changeAvatar(avatarId: Long) {
        viewModelScope.launch {
            _updateStatus.value = UpdateStatus.Loading
            val response = userRepository.changeAvatar(avatarId)
            if (response.isSuccessful) {
                fetchUserProfile() // Refresh to get new avatar URL
                _updateStatus.value = UpdateStatus.Success
            } else {
                _updateStatus.value = UpdateStatus.Error("Failed to change avatar.")
            }
        }
    }

    fun resetUpdateStatus() {
        _updateStatus.value = UpdateStatus.Idle
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            userRepository.logout()
            onSuccess()
        }
    }
}

sealed class UpdateStatus {
    object Idle : UpdateStatus()
    object Loading : UpdateStatus()
    object Success : UpdateStatus()
    data class Error(val message: String) : UpdateStatus()
}

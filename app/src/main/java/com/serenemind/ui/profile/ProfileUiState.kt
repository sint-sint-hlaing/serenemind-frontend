package com.serenemind.ui.profile

import com.serenemind.model.response.UserProfileResponse

sealed interface ProfileUiState {
    object Loading : ProfileUiState
    data class Success(val user: UserProfileResponse) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}
package com.serenemind.ui.profile

import com.serenemind.model.response.UserActivityResponse
import com.serenemind.model.response.UserProfileResponse

sealed interface ProfileUiState {
    object Loading : ProfileUiState
    data class Success(
        val user: UserProfileResponse,
        val activity: UserActivityResponse? = null
    ) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}

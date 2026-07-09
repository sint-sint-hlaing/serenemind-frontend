package com.serenemind.ui.community

import com.serenemind.model.response.PostResponse

sealed class CommunityUiState {
    object Loading : CommunityUiState()
    data class Success(val posts: List<PostResponse>) : CommunityUiState()
    data class Error(val message: String) : CommunityUiState()
}

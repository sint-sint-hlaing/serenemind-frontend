package com.serenemind.ui.community

import com.serenemind.model.response.CommentResponse
import com.serenemind.model.response.PostResponse

sealed class PostDetailUiState {
    object Loading : PostDetailUiState()
    data class Success(val post: PostResponse, val comments: List<CommentResponse>) : PostDetailUiState()
    data class Error(val message: String) : PostDetailUiState()
}

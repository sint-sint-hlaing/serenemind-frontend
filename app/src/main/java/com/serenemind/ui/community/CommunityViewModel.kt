package com.serenemind.ui.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serenemind.model.response.PostResponse
import com.serenemind.repository.CommunityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class CommunityViewModel(
    private val communityRepository: CommunityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CommunityUiState>(CommunityUiState.Loading)
    val uiState: StateFlow<CommunityUiState> = _uiState.asStateFlow()

    init {
        fetchPosts(isInitialLoad = true)
    }

    fun refresh() {
        fetchPosts(isInitialLoad = false)
    }

    fun fetchPosts(isInitialLoad: Boolean = false) {
        viewModelScope.launch {
            if (isInitialLoad) {
                _uiState.value = CommunityUiState.Loading
            }
            communityRepository.getPosts()
                .catch { e ->
                    if (isInitialLoad) _uiState.value = CommunityUiState.Error("Exception: ${e.message}")
                }
                .collect { response ->
                    if (response.isSuccessful && response.body() != null) {
                        _uiState.value = CommunityUiState.Success(response.body()!!)
                    } else if (isInitialLoad) {
                        val errorDetail = response.errorBody()?.string() ?: "Unknown error"
                        _uiState.value = CommunityUiState.Error("Error ${response.code()}: $errorDetail")
                    }
                }
        }
    }

    fun likePost(postId: Long) {
        val currentState = _uiState.value
        if (currentState is CommunityUiState.Success) {
            // Optimistic UI update
            val updatedPosts = currentState.posts.map { post ->
                if (post.id == postId) {
                    val isLiked = !post.isLikedByMe
                    val newLikeCount = if (isLiked) post.likeCount + 1 else post.likeCount - 1
                    post.copy(isLikedByMe = isLiked, likeCount = newLikeCount)
                } else {
                    post
                }
            }
            _uiState.value = CommunityUiState.Success(updatedPosts)

            viewModelScope.launch {
                val response = communityRepository.likePost(postId)
                if (!response.isSuccessful) {
                    // Rollback on failure
                    _uiState.value = currentState
                } else {
                    // Refresh silently to sync with server
                    fetchPosts(isInitialLoad = false)
                }
            }
        }
    }
}

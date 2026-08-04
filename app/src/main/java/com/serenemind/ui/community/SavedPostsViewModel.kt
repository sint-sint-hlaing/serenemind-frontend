package com.serenemind.ui.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serenemind.repository.CommunityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class SavedPostsViewModel(
    private val communityRepository: CommunityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CommunityUiState>(CommunityUiState.Loading)
    val uiState: StateFlow<CommunityUiState> = _uiState.asStateFlow()

    init {
        fetchSavedPosts(isInitialLoad = true)
    }

    fun refresh() {
        fetchSavedPosts(isInitialLoad = false)
    }

    fun fetchSavedPosts(isInitialLoad: Boolean = false) {
        viewModelScope.launch {
            if (isInitialLoad) {
                _uiState.value = CommunityUiState.Loading
            }
            communityRepository.getSavedPosts()
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
                    _uiState.value = currentState
                }
            }
        }
    }

    fun toggleSave(postId: Long) {
        val currentState = _uiState.value
        if (currentState is CommunityUiState.Success) {
            // Optimistic UI update: Remove from saved list if we're unsaving
            val updatedPosts = currentState.posts.filterNot { it.id == postId }
            _uiState.value = CommunityUiState.Success(updatedPosts)

            viewModelScope.launch {
                val response = communityRepository.toggleSavePost(postId)
                if (!response.isSuccessful) {
                    _uiState.value = currentState
                }
            }
        }
    }
}

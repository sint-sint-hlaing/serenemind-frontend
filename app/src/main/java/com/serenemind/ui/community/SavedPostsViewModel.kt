package com.serenemind.ui.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serenemind.network.NetworkResult
import com.serenemind.repository.CommunityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
            communityRepository.getSavedPosts().collect { result ->
                when (result) {
                    is NetworkResult.Loading -> if (isInitialLoad) _uiState.value = CommunityUiState.Loading
                    is NetworkResult.Success -> _uiState.value = CommunityUiState.Success(result.data)
                    is NetworkResult.Error -> if (isInitialLoad) _uiState.value = CommunityUiState.Error(result.message)
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
                communityRepository.likePost(postId).collect { result ->
                    if (result is NetworkResult.Error) {
                        _uiState.value = currentState
                    }
                }
            }
        }
    }

    fun toggleSave(postId: Long) {
        val currentState = _uiState.value
        if (currentState is CommunityUiState.Success) {
            // Optimistic update: Remove from saved list
            val updatedPosts = currentState.posts.filterNot { it.id == postId }
            _uiState.value = CommunityUiState.Success(updatedPosts)

            viewModelScope.launch {
                communityRepository.toggleSavePost(postId).collect { result ->
                    if (result is NetworkResult.Error) {
                        _uiState.value = currentState
                    }
                }
            }
        }
    }

    fun deletePost(postId: Long) {
        val currentState = _uiState.value
        if (currentState is CommunityUiState.Success) {
            val updatedPosts = currentState.posts.filterNot { it.id == postId }
            _uiState.value = CommunityUiState.Success(updatedPosts)

            viewModelScope.launch {
                communityRepository.deletePost(postId).collect { result ->
                    if (result is NetworkResult.Error) {
                        _uiState.value = currentState
                    }
                }
            }
        }
    }
}

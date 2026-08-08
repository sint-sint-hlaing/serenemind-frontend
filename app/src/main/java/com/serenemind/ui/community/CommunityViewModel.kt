package com.serenemind.ui.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serenemind.network.NetworkResult
import com.serenemind.repository.CommunityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CommunityViewModel(
    private val communityRepository: CommunityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CommunityUiState>(CommunityUiState.Loading)
    val uiState: StateFlow<CommunityUiState> = _uiState.asStateFlow()

    private var currentFilter: String? = null

    fun refresh() {
        fetchPosts(isInitialLoad = false, filter = currentFilter)
    }

    fun fetchPosts(isInitialLoad: Boolean = false, filter: String? = null) {
        currentFilter = filter
        viewModelScope.launch {
            communityRepository.getPosts(filter).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        if (isInitialLoad) _uiState.value = CommunityUiState.Loading
                    }
                    is NetworkResult.Success -> {
                        _uiState.value = CommunityUiState.Success(result.data)
                    }
                    is NetworkResult.Error -> {
                        if (isInitialLoad) _uiState.value = CommunityUiState.Error(result.message)
                    }
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
                communityRepository.likePost(postId).collect { result ->
                    if (result is NetworkResult.Error) {
                        // Rollback on failure
                        _uiState.value = currentState
                    } else if (result is NetworkResult.Success) {
                        // Refresh silently to sync with server
                        fetchPosts(isInitialLoad = false)
                    }
                }
            }
        }
    }

    fun toggleSave(postId: Long) {
        val currentState = _uiState.value
        if (currentState is CommunityUiState.Success) {
            // Optimistic UI update
            val updatedPosts = currentState.posts.map { post ->
                if (post.id == postId) {
                    post.copy(isSavedByMe = !post.isSavedByMe)
                } else {
                    post
                }
            }
            _uiState.value = CommunityUiState.Success(updatedPosts)

            viewModelScope.launch {
                communityRepository.toggleSavePost(postId).collect { result ->
                    if (result is NetworkResult.Error) {
                        // Rollback on failure
                        _uiState.value = currentState
                    } else if (result is NetworkResult.Success) {
                        // Refresh silently to sync with server
                        fetchPosts(isInitialLoad = false)
                    }
                }
            }
        }
    }

    fun deletePost(postId: Long) {
        val currentState = _uiState.value
        if (currentState is CommunityUiState.Success) {
            // Optimistic UI update
            val updatedPosts = currentState.posts.filterNot { it.id == postId }
            _uiState.value = CommunityUiState.Success(updatedPosts)

            viewModelScope.launch {
                communityRepository.deletePost(postId).collect { result ->
                    if (result is NetworkResult.Error) {
                        // Rollback on failure
                        _uiState.value = currentState
                    }
                }
            }
        }
    }
}

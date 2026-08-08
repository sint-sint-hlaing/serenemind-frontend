package com.serenemind.ui.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serenemind.model.response.CommentResponse
import com.serenemind.network.NetworkResult
import com.serenemind.repository.CommunityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PostDetailViewModel(
    private val communityRepository: CommunityRepository,
    private val postId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow<PostDetailUiState>(PostDetailUiState.Loading)
    val uiState: StateFlow<PostDetailUiState> = _uiState.asStateFlow()

    init {
        fetchPostAndComments(isInitialLoad = true)
    }

    fun fetchPostAndComments(isInitialLoad: Boolean = false) {
        viewModelScope.launch {
            communityRepository.getPostById(postId).collect { postResult ->
                when (postResult) {
                    is NetworkResult.Loading -> if (isInitialLoad) _uiState.value = PostDetailUiState.Loading
                    is NetworkResult.Success -> {
                        val post = postResult.data
                        communityRepository.getComments(postId).collect { commentResult ->
                            if (commentResult is NetworkResult.Success) {
                                _uiState.value = PostDetailUiState.Success(post, commentResult.data)
                            } else if (commentResult is NetworkResult.Error) {
                                _uiState.value = PostDetailUiState.Success(post, emptyList())
                            }
                        }
                    }
                    is NetworkResult.Error -> if (isInitialLoad) _uiState.value = PostDetailUiState.Error(postResult.message)
                }
            }
        }
    }

    fun likePost() {
        val currentState = _uiState.value
        if (currentState is PostDetailUiState.Success) {
            val currentPost = currentState.post
            val isLiked = !currentPost.isLikedByMe
            val newLikeCount = if (isLiked) currentPost.likeCount + 1 else currentPost.likeCount - 1
            val updatedPost = currentPost.copy(isLikedByMe = isLiked, likeCount = newLikeCount)
            
            _uiState.value = currentState.copy(post = updatedPost)

            viewModelScope.launch {
                communityRepository.likePost(postId).collect { result ->
                    if (result is NetworkResult.Error) {
                        _uiState.value = currentState
                    }
                }
            }
        }
    }

    fun savePost() {
        val currentState = _uiState.value
        if (currentState is PostDetailUiState.Success) {
            val currentPost = currentState.post
            val isSaved = !currentPost.isSavedByMe
            val updatedPost = currentPost.copy(isSavedByMe = isSaved)
            
            _uiState.value = currentState.copy(post = updatedPost)

            viewModelScope.launch {
                communityRepository.toggleSavePost(postId).collect { result ->
                    if (result is NetworkResult.Error) {
                        _uiState.value = currentState
                    }
                }
            }
        }
    }

    fun deletePost(onSuccess: () -> Unit) {
        viewModelScope.launch {
            communityRepository.deletePost(postId).collect { result ->
                if (result is NetworkResult.Success) {
                    onSuccess()
                }
            }
        }
    }

    fun addComment(content: String, isAnonymous: Boolean = false) {
        val currentState = _uiState.value
        if (currentState is PostDetailUiState.Success) {
            // Optimistic update: Add comment locally first
            val newComment = CommentResponse(
                id = -1, // Temporary ID for pending state
                content = content,
                username = if (isAnonymous) "Anonymous" else "You",
                userProfilePicture = null,
                createdAt = "Just now",
                anonymous = isAnonymous
            )
            // Prepend the comment because the list is descending (newest on top)
            val updatedComments = listOf(newComment) + currentState.comments
            _uiState.value = currentState.copy(comments = updatedComments)

            viewModelScope.launch {
                communityRepository.addComment(postId, content, isAnonymous).collect { result ->
                    if (result is NetworkResult.Success) {
                        fetchPostAndComments(isInitialLoad = false) // Sync with server
                    } else if (result is NetworkResult.Error) {
                        // Rollback on failure
                        _uiState.value = currentState
                    }
                }
            }
        }
    }
}

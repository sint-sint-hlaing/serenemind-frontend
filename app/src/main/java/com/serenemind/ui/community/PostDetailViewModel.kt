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
            if (isInitialLoad) {
                _uiState.value = PostDetailUiState.Loading
            }
            
            communityRepository.getPostById(postId)
                .catch { e ->
                    if (isInitialLoad) _uiState.value = PostDetailUiState.Error("Exception: ${e.message}")
                }
                .collect { postResponse ->
                    if (postResponse.isSuccessful && postResponse.body() != null) {
                        val post = postResponse.body()!!
                        communityRepository.getComments(postId)
                            .catch { e ->
                                if (isInitialLoad) _uiState.value = PostDetailUiState.Error("Exception: ${e.message}")
                            }
                            .collect { commentResponse ->
                                if (commentResponse.isSuccessful && commentResponse.body() != null) {
                                    _uiState.value = PostDetailUiState.Success(post, commentResponse.body()!!)
                                } else {
                                    _uiState.value = PostDetailUiState.Success(post, emptyList())
                                }
                            }
                    } else if (isInitialLoad) {
                        _uiState.value = PostDetailUiState.Error("Failed to fetch post")
                    }
                }
        }
    }

    fun likePost() {
        val currentState = _uiState.value
        if (currentState is PostDetailUiState.Success) {
            // Optimistic UI update
            val currentPost = currentState.post
            val isLiked = !currentPost.isLikedByMe
            val newLikeCount = if (isLiked) currentPost.likeCount + 1 else currentPost.likeCount - 1
            val updatedPost = currentPost.copy(isLikedByMe = isLiked, likeCount = newLikeCount)
            
            _uiState.value = currentState.copy(post = updatedPost)

            viewModelScope.launch {
                val response = communityRepository.likePost(postId)
                if (!response.isSuccessful) {
                    // Rollback if failed
                    _uiState.value = currentState
                } else {
                    // Fetch latest data to be sure
                    fetchPostAndComments(isInitialLoad = false)
                }
            }
        }
    }

    fun addComment(content: String) {
        viewModelScope.launch {
            // We don't show loading here anymore, just perform the action
            val response = communityRepository.addComment(postId, content)
            if (response.isSuccessful) {
                fetchPostAndComments(isInitialLoad = false) // Silent refresh
            }
        }
    }
}

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
        fetchPostAndComments()
    }

    fun fetchPostAndComments() {
        viewModelScope.launch {
            _uiState.value = PostDetailUiState.Loading
            communityRepository.getPostById(postId)
                .catch { e ->
                    _uiState.value = PostDetailUiState.Error("Exception: ${e.message}")
                }
                .collect { postResponse ->
                    if (postResponse.isSuccessful && postResponse.body() != null) {
                        val post = postResponse.body()!!
                        communityRepository.getComments(postId)
                            .catch { e ->
                                _uiState.value = PostDetailUiState.Error("Exception: ${e.message}")
                            }
                            .collect { commentResponse ->
                                if (commentResponse.isSuccessful && commentResponse.body() != null) {
                                    _uiState.value = PostDetailUiState.Success(post, commentResponse.body()!!)
                                } else {
                                    _uiState.value = PostDetailUiState.Success(post, emptyList())
                                }
                            }
                    } else {
                        _uiState.value = PostDetailUiState.Error("Failed to fetch post")
                    }
                }
        }
    }

    fun likePost() {
        viewModelScope.launch {
            val response = communityRepository.likePost(postId)
            if (response.isSuccessful) {
                // Refresh data to show updated like count
                fetchPostAndComments()
            }
        }
    }

    fun addComment(content: String) {
        viewModelScope.launch {
            val response = communityRepository.addComment(postId, content)
            if (response.isSuccessful) {
                fetchPostAndComments() // Refresh data
            }
        }
    }
}

package com.serenemind.ui.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serenemind.model.request.CreatePostRequest
import com.serenemind.network.NetworkResult
import com.serenemind.repository.CommunityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class CreatePostViewModel(
    private val communityRepository: CommunityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CreatePostUiState>(CreatePostUiState.Idle)
    val uiState: StateFlow<CreatePostUiState> = _uiState.asStateFlow()

    fun createPost(content: String, isAnonymous: Boolean, imagePart: MultipartBody.Part?) {
        if (content.isBlank()) {
            _uiState.value = CreatePostUiState.Error("Content cannot be empty")
            return
        }

        viewModelScope.launch {
            val request = CreatePostRequest(content = content, anonymous = isAnonymous)
            communityRepository.createPost(request, imagePart).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> _uiState.value = CreatePostUiState.Loading
                    is NetworkResult.Success -> _uiState.value = CreatePostUiState.Success
                    is NetworkResult.Error -> _uiState.value = CreatePostUiState.Error(result.message)
                }
            }
        }
    }

    fun resetState() {
        _uiState.value = CreatePostUiState.Idle
    }
}

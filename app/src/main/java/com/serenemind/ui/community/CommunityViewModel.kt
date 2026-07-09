package com.serenemind.ui.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
        fetchPosts()
    }

    fun fetchPosts() {
        viewModelScope.launch {
            _uiState.value = CommunityUiState.Loading
            communityRepository.getPosts()
                .catch { e ->
                    _uiState.value = CommunityUiState.Error("Exception: ${e.message}")
                }
                .collect { response ->
                    if (response.isSuccessful && response.body() != null) {
                        _uiState.value = CommunityUiState.Success(response.body()!!)
                    } else {
                        val errorDetail = response.errorBody()?.string() ?: "Unknown error"
                        _uiState.value = CommunityUiState.Error("Error ${response.code()}: $errorDetail")
                    }
                }
        }
    }
}

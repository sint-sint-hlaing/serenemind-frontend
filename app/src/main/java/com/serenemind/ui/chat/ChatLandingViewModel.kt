package com.serenemind.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serenemind.network.NetworkResult
import com.serenemind.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatLandingViewModel(private val repository: ChatRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<ChatLandingUiState>(ChatLandingUiState.Loading)
    val uiState: StateFlow<ChatLandingUiState> = _uiState.asStateFlow()

    init {
        fetchConversations()
    }

    fun fetchConversations() {
        viewModelScope.launch {
            repository.getConversations().collect { result ->
                when (result) {
                    is NetworkResult.Loading -> _uiState.value = ChatLandingUiState.Loading
                    is NetworkResult.Success -> _uiState.value = ChatLandingUiState.Success(result.data)
                    is NetworkResult.Error -> _uiState.value = ChatLandingUiState.Error(result.message)
                }
            }
        }
    }

    fun refresh() {
        fetchConversations()
    }

    fun deleteConversation(id: Long) {
        viewModelScope.launch {
            repository.deleteConversation(id).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> { /* Optionally update a separate loading state */ }
                    is NetworkResult.Success -> {
                        fetchConversations()
                    }
                    is NetworkResult.Error -> {
                        _uiState.value = ChatLandingUiState.Error(result.message)
                    }
                }
            }
        }
    }
}

package com.serenemind.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
            _uiState.value = ChatLandingUiState.Loading
            repository.getConversations().collect { response ->
                if (response.isSuccessful) {
                    val conversations = response.body() ?: emptyList()
                    _uiState.value = ChatLandingUiState.Success(conversations)
                } else {
                    _uiState.value = ChatLandingUiState.Error("Failed to load previous conversations")
                }
            }
        }
    }
}

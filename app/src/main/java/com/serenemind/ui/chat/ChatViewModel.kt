package com.serenemind.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serenemind.model.response.ChatMessageResponse
import com.serenemind.network.NetworkResult
import com.serenemind.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(private val repository: ChatRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Initial)
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var currentConversationId: Long? = null
    private val messages = mutableListOf<Message>()

    fun setConversationId(id: Long?) {
        currentConversationId = id
        if (id != null) {
            fetchMessages(id)
        }
    }

    private fun fetchMessages(conversationId: Long) {
        viewModelScope.launch {
            repository.getConversationMessages(conversationId).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> _uiState.value = ChatUiState.Loading
                    is NetworkResult.Success -> {
                        messages.clear()
                        messages.addAll(result.data.map { it.toMessage() })
                        _uiState.value = ChatUiState.Active(currentConversationId, messages.toList())
                    }
                    is NetworkResult.Error -> _uiState.value = ChatUiState.Error(result.message)
                }
            }
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        // Add user message optimistically
        val userMessage = Message(
            id = System.currentTimeMillis(),
            text = text,
            isUser = true,
            timestamp = "Just now"
        )
        messages.add(userMessage)
        _uiState.value = ChatUiState.Active(currentConversationId, messages.toList(), isTyping = true)

        viewModelScope.launch {
            repository.sendMessage(text, currentConversationId).collect { result ->
                if (result is NetworkResult.Success) {
                    val data = result.data
                    currentConversationId = data.id
                    
                    // Remove the optimistic message and sync from response
                    messages.remove(userMessage)
                    
                    // Filter out existing messages by ID and add new ones
                    val newMessages = data.messages.map { it.toMessage() }
                    val existingIds = messages.map { it.id }.toSet()
                    val uniqueNewMessages = newMessages.filter { it.id !in existingIds }
                    
                    messages.addAll(uniqueNewMessages)
                    _uiState.value = ChatUiState.Active(currentConversationId, messages.toList(), isTyping = false)
                } else if (result is NetworkResult.Error) {
                    _uiState.value = ChatUiState.Active(currentConversationId, messages.toList(), isTyping = false)
                }
            }
        }
    }

    private fun ChatMessageResponse.toMessage() = Message(
        id = id,
        text = content,
        isUser = sender == "user",
        timestamp = timestamp
    )
}

package com.serenemind.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serenemind.model.response.ChatMessageResponse
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
            _uiState.value = ChatUiState.Loading
            repository.getConversationMessages(conversationId).collect { response ->
                if (response.isSuccessful && response.body() != null) {
                    messages.clear()
                    messages.addAll(response.body()!!.map { it.toMessage() })
                    _uiState.value = ChatUiState.Active(currentConversationId, messages.toList())
                } else {
                    _uiState.value = ChatUiState.Error("Failed to load previous messages")
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
            repository.sendMessage(text, currentConversationId).collect { response ->
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    currentConversationId = data.id
                    
                    // Remove the optimistic message and sync from response
                    messages.remove(userMessage)
                    
                    // Filter out existing messages by ID and add new ones
                    val newMessages = data.messages.map { it.toMessage() }
                    val existingIds = messages.map { it.id }.toSet()
                    val uniqueNewMessages = newMessages.filter { it.id !in existingIds }
                    
                    messages.addAll(uniqueNewMessages)
                    _uiState.value = ChatUiState.Active(currentConversationId, messages.toList(), isTyping = false)
                } else {
                    _uiState.value = ChatUiState.Active(currentConversationId, messages.toList(), isTyping = false)
                    // Optional: You could show a toast or a small error message here instead of switching full state
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

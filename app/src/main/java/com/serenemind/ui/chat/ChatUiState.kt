package com.serenemind.ui.chat

import com.serenemind.model.response.ConversationResponse

sealed interface ChatLandingUiState {
    object Loading : ChatLandingUiState
    data class Success(val conversations: List<ConversationResponse>) : ChatLandingUiState
    data class Error(val message: String) : ChatLandingUiState
}

data class Message(
    val id: Long,
    val text: String,
    val isUser: Boolean,
    val timestamp: String?
)

sealed interface ChatUiState {
    object Initial : ChatUiState
    object Loading : ChatUiState
    data class Active(
        val conversationId: Long?,
        val messages: List<Message>
    ) : ChatUiState
    data class Error(val message: String) : ChatUiState
}

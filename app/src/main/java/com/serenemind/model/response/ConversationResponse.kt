package com.serenemind.model.response

data class ConversationResponse(
    val id: Long,
    val title: String?,
    val createdAt: String?,
    val messages: List<MessageResponse>
)

data class MessageResponse(
    val id: Long,
    val sender: String, // "user" or "assistant"
    val content: String,
    val timestamp: String?
)

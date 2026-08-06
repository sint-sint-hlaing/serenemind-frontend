package com.serenemind.model.request

data class ChatSendRequest(
    val conversationId: Long?,
    val message: String
)

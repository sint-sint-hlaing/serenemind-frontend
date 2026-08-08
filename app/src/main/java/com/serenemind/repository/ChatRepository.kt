package com.serenemind.repository

import com.serenemind.model.request.ChatSendRequest
import com.serenemind.model.response.ChatMessageResponse
import com.serenemind.model.response.ConversationResponse
import com.serenemind.network.ChatApiService
import com.serenemind.network.NetworkResult
import com.serenemind.network.SafeApiCall
import kotlinx.coroutines.flow.Flow

class ChatRepository(private val apiService: ChatApiService) : SafeApiCall() {
    fun sendMessage(message: String, conversationId: Long? = null): Flow<NetworkResult<ConversationResponse>> = safeApiCall {
        apiService.sendMessage(ChatSendRequest(conversationId, message))
    }

    fun getConversations(): Flow<NetworkResult<List<ConversationResponse>>> = safeApiCall {
        apiService.getConversations()
    }

    fun getConversationMessages(conversationId: Long): Flow<NetworkResult<List<ChatMessageResponse>>> = safeApiCall {
        apiService.getConversationMessages(conversationId)
    }
}

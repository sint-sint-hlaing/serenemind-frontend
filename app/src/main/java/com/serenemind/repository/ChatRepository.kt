package com.serenemind.repository

import com.serenemind.model.request.ChatSendRequest
import com.serenemind.network.ChatApiService
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class ChatRepository(private val apiService: ChatApiService) {
    fun sendMessage(message: String, conversationId: Long? = null) = flow {
        try {
            emit(apiService.sendMessage(ChatSendRequest(conversationId, message)))
        } catch (e: Exception) {
            emit(Response.error(500, okhttp3.ResponseBody.create(null, "Network Error")))
        }
    }

    fun getConversations() = flow {
        try {
            emit(apiService.getConversations())
        } catch (e: Exception) {
            emit(Response.error(500, okhttp3.ResponseBody.create(null, "Network Error")))
        }
    }

    fun getConversationMessages(conversationId: Long) = flow {
        try {
            emit(apiService.getConversationMessages(conversationId))
        } catch (e: Exception) {
            emit(Response.error(500, okhttp3.ResponseBody.create(null, "Network Error")))
        }
    }
}

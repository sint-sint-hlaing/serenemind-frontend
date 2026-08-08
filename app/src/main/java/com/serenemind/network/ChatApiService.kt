package com.serenemind.network

import com.serenemind.model.request.ChatSendRequest
import com.serenemind.model.response.ConversationResponse
import retrofit2.Response
import retrofit2.http.*

interface ChatApiService {
    @POST("api/chat/send")
    suspend fun sendMessage(
        @Body request: ChatSendRequest
    ): Response<ConversationResponse>

    @GET("api/chat/conversations")
    suspend fun getConversations(): Response<List<ConversationResponse>>

    @GET("api/chat/conversations/{id}/messages")
    suspend fun getConversationMessages(
        @Path("id") conversationId: Long
    ): Response<List<com.serenemind.model.response.ChatMessageResponse>>

    @DELETE("api/chat/conversations/{id}")
    suspend fun deleteConversation(
        @Path("id") conversationId: Long
    ): Response<Unit>
}

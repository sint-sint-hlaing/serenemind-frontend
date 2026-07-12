package com.serenemind.repository

import com.serenemind.datastore.TokenManager
import com.serenemind.model.response.NotificationResponse
import com.serenemind.network.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class NotificationRepository(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {
    fun getNotifications(filter: String? = null): Flow<Response<List<NotificationResponse>>> = flow {
        val token = tokenManager.getToken() ?: ""
        emit(apiService.getNotifications("Bearer $token", filter))
    }

    suspend fun markAsRead(id: Long): Response<Unit> {
        val token = tokenManager.getToken() ?: ""
        return apiService.markAsRead(id, "Bearer $token")
    }

    suspend fun clickNotification(id: Long): Response<NotificationResponse> {
        val token = tokenManager.getToken() ?: ""
        return apiService.clickNotification(id, "Bearer $token")
    }

    suspend fun markAllAsRead(): Response<Unit> {
        val token = tokenManager.getToken() ?: ""
        return apiService.markAllAsRead("Bearer $token")
    }
}

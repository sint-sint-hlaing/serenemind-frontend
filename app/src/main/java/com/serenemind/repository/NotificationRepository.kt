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
        emit(apiService.getNotifications(filter))
    }

    suspend fun markAsRead(id: Long): Response<Unit> {
        return apiService.markAsRead(id)
    }

    suspend fun clickNotification(id: Long): Response<NotificationResponse> {
        return apiService.clickNotification(id)
    }

    suspend fun markAllAsRead(): Response<Unit> {
        return apiService.markAllAsRead()
    }
}

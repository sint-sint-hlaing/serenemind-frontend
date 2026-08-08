package com.serenemind.repository

import com.serenemind.datastore.TokenManager
import com.serenemind.model.response.NotificationResponse
import com.serenemind.network.ApiService
import com.serenemind.network.NetworkResult
import com.serenemind.network.SafeApiCall
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class NotificationRepository(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : SafeApiCall() {
    fun getNotifications(filter: String? = null): Flow<NetworkResult<List<NotificationResponse>>> = safeApiCall {
        apiService.getNotifications(filter)
    }

    fun markAsRead(id: Long): Flow<NetworkResult<Unit>> = safeApiCall {
        apiService.markAsRead(id)
    }

    fun clickNotification(id: Long): Flow<NetworkResult<NotificationResponse>> = safeApiCall {
        apiService.clickNotification(id)
    }

    fun markAllAsRead(): Flow<NetworkResult<Unit>> = safeApiCall {
        apiService.markAllAsRead()
    }
}

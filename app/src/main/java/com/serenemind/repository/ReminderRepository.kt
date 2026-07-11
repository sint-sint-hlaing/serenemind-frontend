package com.serenemind.repository

import com.serenemind.datastore.TokenManager
import com.serenemind.model.request.ReminderRequest
import com.serenemind.model.response.ReminderResponse
import com.serenemind.network.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class ReminderRepository(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {
    fun getReminders(): Flow<Response<List<ReminderResponse>>> = flow {
        val token = tokenManager.getToken() ?: ""
        emit(apiService.getReminders("Bearer $token"))
    }

    suspend fun createReminder(request: ReminderRequest): Response<ReminderResponse> {
        val token = tokenManager.getToken() ?: ""
        return apiService.createReminder("Bearer $token", request)
    }

    suspend fun deleteReminder(id: Long): Response<Unit> {
        val token = tokenManager.getToken() ?: ""
        return apiService.deleteReminder("Bearer $token", id)
    }

    suspend fun toggleReminder(id: Long): Response<ReminderResponse> {
        val token = tokenManager.getToken() ?: ""
        return apiService.toggleReminder("Bearer $token", id)
    }
}

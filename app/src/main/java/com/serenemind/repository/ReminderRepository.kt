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
        try {
            emit(apiService.getReminders())
        } catch (e: Exception) {
            emit(Response.error(500, okhttp3.ResponseBody.create(null, "Network Error")))
        }
    }

    suspend fun createReminder(request: ReminderRequest): Response<ReminderResponse> {
        return try {
            apiService.createReminder(request)
        } catch (e: Exception) {
            Response.error(500, okhttp3.ResponseBody.create(null, "Network Error"))
        }
    }

    suspend fun deleteReminder(id: Long): Response<Unit> {
        return try {
            apiService.deleteReminder(id)
        } catch (e: Exception) {
            Response.error(500, okhttp3.ResponseBody.create(null, "Network Error"))
        }
    }

    suspend fun toggleReminder(id: Long): Response<ReminderResponse> {
        return try {
            apiService.toggleReminder(id)
        } catch (e: Exception) {
            Response.error(500, okhttp3.ResponseBody.create(null, "Network Error"))
        }
    }
}

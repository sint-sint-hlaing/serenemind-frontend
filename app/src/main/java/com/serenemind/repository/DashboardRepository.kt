package com.serenemind.repository

import com.serenemind.datastore.TokenManager
import com.serenemind.network.ApiService
import com.serenemind.model.response.DashboardResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import retrofit2.Response

class DashboardRepository(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {

    fun getDashboardData(): Flow<Response<DashboardResponse>> = flow {
        try {
            val response = apiService.getDashboardData()
            emit(response)
        } catch (e: Exception) {
            // Log error if possible or emit a custom error response
            val errorBody = okhttp3.ResponseBody.create(
                "application/json".toMediaTypeOrNull(),
                "{\"message\": \"${e.localizedMessage ?: "Unknown Network Error"}\"}"
            )
            emit(Response.error(500, errorBody))
        }
    }
}

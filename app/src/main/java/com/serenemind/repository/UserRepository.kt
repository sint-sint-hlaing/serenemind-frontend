package com.serenemind.repository

import com.serenemind.datastore.TokenManager
import com.serenemind.network.ApiService
import com.serenemind.model.response.UserProfileResponse
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class UserRepository(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {
    fun getUserProfile() = flow {
        try {
            val response = apiService.getUserProfile()
            emit(response)
        } catch (e: Exception) {
            // Rethrow or emit specific error response. Emitting error response to keep Flow alive.
            emit(Response.error<UserProfileResponse>(500, okhttp3.ResponseBody.create(null, "Network Error")))
        }
    }

    suspend fun logout() {
        tokenManager.clearToken()
    }
}

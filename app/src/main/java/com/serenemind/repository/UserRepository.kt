package com.serenemind.repository

import com.serenemind.datastore.TokenManager
import com.serenemind.model.request.SelectAvatarRequest
import com.serenemind.model.request.UpdateProfileRequest
import com.serenemind.model.response.AvatarResponse
import com.serenemind.model.response.MessageResponse
import com.serenemind.model.response.UserProfileResponse
import com.serenemind.network.ApiService
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
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
            emit(Response.error<UserProfileResponse>(500, okhttp3.ResponseBody.create(null, "Network Error")))
        }
    }

    suspend fun updateUserProfile(request: UpdateProfileRequest): Response<UserProfileResponse> {
        return try {
            apiService.updateUserProfile(request)
        } catch (e: Exception) {
            Response.error(500, okhttp3.ResponseBody.create(null, "Network Error"))
        }
    }

    suspend fun uploadProfileImage(image: MultipartBody.Part): Response<MessageResponse> {
        return try {
            apiService.uploadProfileImage(image)
        } catch (e: Exception) {
            Response.error(500, okhttp3.ResponseBody.create(null, "Network Error"))
        }
    }

    suspend fun removeProfileImage(): Response<MessageResponse> {
        return try {
            apiService.removeProfileImage()
        } catch (e: Exception) {
            Response.error(500, okhttp3.ResponseBody.create(null, "Network Error"))
        }
    }

    suspend fun changeAvatar(avatarId: Long): Response<MessageResponse> {
        return try {
            apiService.changeAvatar(SelectAvatarRequest(avatarId))
        } catch (e: Exception) {
            Response.error(500, okhttp3.ResponseBody.create(null, "Network Error"))
        }
    }

    fun getAvatars() = flow {
        try {
            emit(apiService.getAvatars())
        } catch (e: Exception) {
            emit(Response.error<List<AvatarResponse>>(500, okhttp3.ResponseBody.create(null, "Network Error")))
        }
    }

    suspend fun logout() {
        tokenManager.clearTokens()
    }
}

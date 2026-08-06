package com.serenemind.repository

import com.serenemind.datastore.TokenManager
import com.serenemind.model.response.UserProfileResponse
import com.serenemind.model.response.UserActivityResponse
import com.serenemind.model.response.PersonalInfoResponse
import com.serenemind.network.ApiService
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
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

    fun getUserActivity() = flow {
        try {
            emit(apiService.getUserActivity())
        } catch (e: Exception) {
            emit(Response.error<UserActivityResponse>(500, okhttp3.ResponseBody.create(null, "Network Error")))
        }
    }

    fun getPersonalInfo() = flow {
        try {
            emit(apiService.getPersonalInfo())
        } catch (e: Exception) {
            emit(Response.error<PersonalInfoResponse>(500, okhttp3.ResponseBody.create(null, "Network Error")))
        }
    }

    suspend fun updateUserProfile(
        fullname: String,
        username: String,
        birthday: String,
        bio: String,
        avatarPart: MultipartBody.Part?
    ): Response<UserProfileResponse> {
        return try {
            val fullnameBody = fullname.toRequestBody("text/plain".toMediaTypeOrNull())
            val usernameBody = username.toRequestBody("text/plain".toMediaTypeOrNull())
            val birthdayBody = birthday.toRequestBody("text/plain".toMediaTypeOrNull())
            val bioBody = bio.toRequestBody("text/plain".toMediaTypeOrNull())
            
            apiService.updateUserProfile(fullnameBody, usernameBody, birthdayBody, bioBody, avatarPart)
        } catch (e: Exception) {
            Response.error(500, okhttp3.ResponseBody.create(null, "Network Error"))
        }
    }

    suspend fun logout() {
        tokenManager.clearTokens()
    }
}

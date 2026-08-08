package com.serenemind.repository

import com.serenemind.datastore.TokenManager
import com.serenemind.model.response.UserProfileResponse
import com.serenemind.model.response.UserActivityResponse
import com.serenemind.model.response.PersonalInfoResponse
import com.serenemind.network.ApiService
import com.serenemind.network.NetworkResult
import com.serenemind.network.SafeApiCall
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class UserRepository(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : SafeApiCall() {

    fun getUserProfile(): Flow<NetworkResult<UserProfileResponse>> = safeApiCall {
        apiService.getUserProfile()
    }

    fun getUserActivity(): Flow<NetworkResult<UserActivityResponse>> = safeApiCall {
        apiService.getUserActivity()
    }

    fun getPersonalInfo(): Flow<NetworkResult<PersonalInfoResponse>> = safeApiCall {
        apiService.getPersonalInfo()
    }

    fun updateUserProfile(
        fullname: String,
        username: String,
        birthday: String,
        bio: String,
        avatarPart: MultipartBody.Part?
    ): Flow<NetworkResult<UserProfileResponse>> = safeApiCall {
        val fullnameBody = fullname.toRequestBody("text/plain".toMediaTypeOrNull())
        val usernameBody = username.toRequestBody("text/plain".toMediaTypeOrNull())
        val birthdayBody = birthday.toRequestBody("text/plain".toMediaTypeOrNull())
        val bioBody = bio.toRequestBody("text/plain".toMediaTypeOrNull())
        
        apiService.updateUserProfile(fullnameBody, usernameBody, birthdayBody, bioBody, avatarPart)
    }

    suspend fun logout() {
        tokenManager.clearTokens()
    }
}

package com.serenemind.repository

import com.google.gson.Gson
import com.serenemind.datastore.TokenManager
import com.serenemind.model.request.CommentRequest
import com.serenemind.model.request.CreatePostRequest
import com.serenemind.model.response.CommentResponse
import com.serenemind.model.response.PostResponse
import com.serenemind.network.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response

class CommunityRepository(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {
    fun getPosts(): Flow<Response<List<PostResponse>>> = flow {
        val token = tokenManager.getToken() ?: ""
        emit(apiService.getPosts("Bearer $token"))
    }

    fun getPostById(postId: Long): Flow<Response<PostResponse>> = flow {
        val token = tokenManager.getToken() ?: ""
        emit(apiService.getPostById(postId, "Bearer $token"))
    }

    fun getComments(postId: Long): Flow<Response<List<CommentResponse>>> = flow {
        val token = tokenManager.getToken() ?: ""
        emit(apiService.getComments(postId, "Bearer $token"))
    }

    suspend fun likePost(postId: Long): Response<Unit> {
        val token = tokenManager.getToken() ?: ""
        return apiService.likePost(postId, "Bearer $token")
    }

    suspend fun addComment(postId: Long, content: String): Response<CommentResponse> {
        val token = tokenManager.getToken() ?: ""
        return apiService.addComment(postId, "Bearer $token", CommentRequest(content))
    }

    suspend fun createPost(request: CreatePostRequest, imagePart: MultipartBody.Part?): Response<PostResponse> {
        val token = tokenManager.getToken() ?: ""
        val json = Gson().toJson(request)
        val postPart = json.toRequestBody("application/json".toMediaTypeOrNull())
        return apiService.createPost("Bearer $token", postPart, imagePart)
    }
}

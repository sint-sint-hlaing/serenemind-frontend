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
        try {
            emit(apiService.getPosts())
        } catch (e: Exception) {
            emit(Response.error(500, okhttp3.ResponseBody.create(null, "Network Error")))
        }
    }

    fun getPostById(postId: Long): Flow<Response<PostResponse>> = flow {
        try {
            emit(apiService.getPostById(postId))
        } catch (e: Exception) {
            emit(Response.error(500, okhttp3.ResponseBody.create(null, "Network Error")))
        }
    }

    fun getComments(postId: Long): Flow<Response<List<CommentResponse>>> = flow {
        try {
            emit(apiService.getComments(postId))
        } catch (e: Exception) {
            emit(Response.error(500, okhttp3.ResponseBody.create(null, "Network Error")))
        }
    }

    suspend fun likePost(postId: Long): Response<Unit> {
        return try {
            apiService.likePost(postId)
        } catch (e: Exception) {
            Response.error(500, okhttp3.ResponseBody.create(null, "Network Error"))
        }
    }

    suspend fun addComment(
        postId: Long,
        content: String,
        isAnonymous: Boolean = false
    ): Response<CommentResponse> {
    suspend fun addComment(postId: Long, content: String, isAnonymous: Boolean = false): Response<CommentResponse> {
        return try {
            apiService.addComment(
                postId,
                CommentRequest(content, isAnonymous)
            )
            apiService.addComment(postId, CommentRequest(content, isAnonymous))
        } catch (e: Exception) {
            Response.error(500, okhttp3.ResponseBody.create(null, "Network Error"))
        }
    }

    suspend fun createPost(
        request: CreatePostRequest,
        imagePart: MultipartBody.Part?
    ): Response<PostResponse> {
        return try {
            val json = Gson().toJson(request)
            val postPart = json.toRequestBody("application/json".toMediaTypeOrNull())
            apiService.createPost(postPart, imagePart)
        } catch (e: Exception) {
            Response.error(500, okhttp3.ResponseBody.create(null, "Network Error"))
        }
    }
}

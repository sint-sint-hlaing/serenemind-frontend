package com.serenemind.repository

import com.google.gson.Gson
import com.serenemind.datastore.TokenManager
import com.serenemind.model.request.CommentRequest
import com.serenemind.model.request.CreatePostRequest
import com.serenemind.model.response.CommentResponse
import com.serenemind.model.response.PostResponse
import com.serenemind.network.ApiService
import com.serenemind.network.NetworkResult
import com.serenemind.network.SafeApiCall
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class CommunityRepository(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : SafeApiCall() {
    fun getPosts(filter: String? = null): Flow<NetworkResult<List<PostResponse>>> = safeApiCall {
        apiService.getPosts(filter)
    }

    fun getPostById(postId: Long): Flow<NetworkResult<PostResponse>> = safeApiCall {
        apiService.getPostById(postId)
    }

    fun getComments(postId: Long): Flow<NetworkResult<List<CommentResponse>>> = safeApiCall {
        apiService.getComments(postId)
    }

    fun likePost(postId: Long): Flow<NetworkResult<Unit>> = safeApiCall {
        apiService.likePost(postId)
    }

    fun toggleSavePost(postId: Long): Flow<NetworkResult<Unit>> = safeApiCall {
        apiService.toggleSavePost(postId)
    }

    fun getSavedPosts(): Flow<NetworkResult<List<PostResponse>>> = safeApiCall {
        apiService.getSavedPosts()
    }

    fun addComment(
        postId: Long,
        content: String,
        isAnonymous: Boolean = false
    ): Flow<NetworkResult<CommentResponse>> = safeApiCall {
        apiService.addComment(
            postId,
            CommentRequest(content, isAnonymous)
        )
    }

    fun createPost(
        request: CreatePostRequest,
        imagePart: MultipartBody.Part?
    ): Flow<NetworkResult<PostResponse>> = safeApiCall {
        val json = Gson().toJson(request)
        val postPart = json.toRequestBody("application/json".toMediaTypeOrNull())
        apiService.createPost(postPart, imagePart)
    }

    fun deletePost(postId: Long): Flow<NetworkResult<Unit>> = safeApiCall {
        apiService.deletePost(postId)
    }
}

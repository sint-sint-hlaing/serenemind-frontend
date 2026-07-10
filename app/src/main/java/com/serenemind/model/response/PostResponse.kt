package com.serenemind.model.response

import com.google.gson.annotations.SerializedName

data class PostResponse(
    val id: Long,
    val content: String,
    val imageUrl: String?,
    val username: String,
    val userProfilePicture: String?,
    val likeCount: Int,
    val commentCount: Int,
    @SerializedName("likedByMe") val isLikedByMe: Boolean,
    val createdAt: String
)

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
    @SerializedName("savedByMe") val isSavedByMe: Boolean = false,
    val createdAt: String,
    val anonymous: Boolean = false
)

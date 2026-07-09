package com.serenemind.model.response

data class PostResponse(
    val id: Long,
    val content: String,
    val imageUrl: String?,
    val username: String,
    val userProfilePicture: String?,
    val likeCount: Int,
    val commentCount: Int,
    val isLikedByMe: Boolean,
    val createdAt: String
)

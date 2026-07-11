package com.serenemind.model.response

data class CommentResponse(
    val id: Long,
    val content: String,
    val username: String,
    val userProfilePicture: String?,
    val createdAt: String,
    val anonymous: Boolean = false
)

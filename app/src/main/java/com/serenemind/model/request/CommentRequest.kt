package com.serenemind.model.request

data class CommentRequest(
    val content: String,
    val anonymous: Boolean = false
)

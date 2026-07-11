package com.serenemind.model.request

data class CreatePostRequest(
    val content: String,
    val anonymous: Boolean = false
)

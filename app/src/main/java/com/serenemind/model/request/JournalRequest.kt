package com.serenemind.model.request

data class JournalRequest(
    val title: String,
    val content: String,
    val mood: String? = null,
    val tags: List<String>? = null,
    val isPrivate: Boolean = false
)

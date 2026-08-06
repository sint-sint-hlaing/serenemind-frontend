package com.serenemind.model.response

import com.google.gson.annotations.SerializedName

data class JournalResponse(
    val id: Long,
    val title: String,
    val content: String,
    val mood: String?,
    val tags: List<String>?,
    val imageUrl: String?,
    val isFavorite: Boolean,
    val isPrivate: Boolean,
    val createdAt: String,
    val updatedAt: String
)

data class JournalPhotoResponse(
    val id: Long,
    val imageUrl: String
)

data class JournalAnalysisResponse(
    val id: Long,
    val sentiment: String,
    val keywords: List<String>,
    val suggestions: List<String>
)

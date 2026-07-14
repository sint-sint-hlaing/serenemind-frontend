package com.serenemind.model.response

import com.google.gson.annotations.SerializedName

data class JournalResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("tags") val tags: List<String>?,
    @SerializedName("isPrivate") val isPrivate: Boolean,
    @SerializedName("favourite") val favourite: Boolean,
    @SerializedName("photoUrl") val photoUrl: String?,
    @SerializedName("createdAt") val createdAt: String
)

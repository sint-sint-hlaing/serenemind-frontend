package com.serenemind.model.request

import com.google.gson.annotations.SerializedName

data class JournalRequest(
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("tags") val tags: List<String> = emptyList(),
    @SerializedName("favourite") val favourite: Boolean = false
)

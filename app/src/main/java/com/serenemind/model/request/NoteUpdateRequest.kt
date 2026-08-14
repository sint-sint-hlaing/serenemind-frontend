package com.serenemind.model.request
import com.google.gson.annotations.SerializedName
data class NoteUpdateRequest(
    @SerializedName("content")
    val content: String
)

package com.serenemind.model.request
import com.google.gson.annotations.SerializedName

data class GoalNoteRequest(
    @SerializedName("content")
    val content: String
)

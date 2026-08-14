package com.serenemind.model.request

import com.google.gson.annotations.SerializedName

data class MoodRequest(
    @SerializedName("mood")
    val mood: String,

    @SerializedName("intensity")
    val intensity: Int,

    @SerializedName("score")
    val score: Int? = null,

    @SerializedName("note")
    val note: String? = null
)

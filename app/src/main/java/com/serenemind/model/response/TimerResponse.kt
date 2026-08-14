package com.serenemind.model.response
import com.google.gson.annotations.SerializedName
data class TimerResponse(
    @SerializedName("minutes")
    val minutes: Int? = null,

    @SerializedName("message")
    val message: String? = null
)

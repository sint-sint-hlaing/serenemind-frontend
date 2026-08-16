package com.serenemind.model.request

import com.google.gson.annotations.SerializedName

data class GoalProgressRequest(
    @SerializedName("completed")
    val completed: Boolean,
    @SerializedName("note")
    val note: String? = null
)

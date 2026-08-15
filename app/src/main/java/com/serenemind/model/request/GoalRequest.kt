package com.serenemind.model.request

import com.google.gson.annotations.SerializedName
import com.serenemind.model.entity.enums.Frequency

data class GoalRequest(
    @SerializedName("title")
    val title: String,

    @SerializedName("frequency")
    val frequency: Frequency,  // Use enum type

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("targetDays")
    val targetDays: Int,

    @SerializedName("unit")
    val unit: String = "days",
    val color:String?=null,

    @SerializedName("startDate")
    val startDate: String? = null,

    @SerializedName("icon")
    val icon: String = "📚",

    @SerializedName("silentMode")
    val silentMode: Boolean = false
)

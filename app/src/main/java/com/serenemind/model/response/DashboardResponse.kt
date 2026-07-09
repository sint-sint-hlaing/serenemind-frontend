package com.serenemind.model.response

import com.google.gson.annotations.SerializedName

data class DashboardResponse(
    @SerializedName("username") val userName: String? = null,
    @SerializedName("currentMood") val currentMood: String? = null,
    @SerializedName("moodPercentage") val moodPercentage: Int? = null,
    @SerializedName("weeklyOverview") val weeklyOverview: List<WeeklyData>? = null,
    @SerializedName("quickActions") val quickActions: List<ActionItem>? = null
)

data class WeeklyData(
    @SerializedName("day") val day: String? = null,
    @SerializedName("value") val value: Float? = null
)

data class ActionItem(
    @SerializedName("name") val name: String? = null,
    @SerializedName("iconUrl") val iconUrl: String? = null
)

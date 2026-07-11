package com.serenemind.model.response

import com.google.gson.annotations.SerializedName

data class DashboardResponse(
    @SerializedName("username") val userName: String? = null,
    @SerializedName("currentMood") val currentMood: String? = null,
    @SerializedName("moodPercentage") val moodPercentage: Int = 0,
    @SerializedName("currentStreak") val currentStreak: Int = 0,
    @SerializedName("isNewBest") val isNewBest: Boolean = false,
    @SerializedName("weeklyDataList") val weeklyOverview: List<WeeklyData>? = null, // Changed from weeklyOverview
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

package com.serenemind.model.response

import com.google.gson.annotations.SerializedName

data class DashboardResponse(
    val username: String? = null,
    val greeting: String? = null,
    val date: String? = null,
    @SerializedName("currentMood") val mood: String? = null,
    @SerializedName("moodPercentage") val percentage: Int? = null,
    @SerializedName("weeklyDataList") val weeklyOverview: List<WeeklyMoodResponse> = emptyList(),
    @SerializedName("quickActions") val quickActions: List<QuickActionResponse> = emptyList()
)

data class TodayMoodResponse(
    val mood: String,
    val percentage: Int,
    val message: String
)

data class QuickActionResponse(
    @SerializedName("name") val title: String? = "",
    val route: String? = "",
    val icon: String? = ""
)

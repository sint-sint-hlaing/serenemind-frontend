package com.serenemind.model.response

import com.google.gson.annotations.SerializedName

data class DashboardResponse(
    @SerializedName("username", alternate = ["userName"]) val username: String? = "",
    @SerializedName("currentMood") val currentMood: String? = null,
    @SerializedName("moodPercentage") val moodPercentage: Int? = 0,
    @SerializedName("currentStreak") val currentStreak: Int? = 0,
    @SerializedName("isNewBest") val isNewBest: Boolean? = false,
    @SerializedName("greeting") val greeting: String? = "Hello",
    @SerializedName("date") val date: String? = "",
    @SerializedName("todayMood") val todayMood: TodayMoodResponse? = null,
    @SerializedName("weeklyDataList", alternate = ["weeklyOverview"]) val weeklyOverview: List<WeeklyMoodResponse>? = emptyList(),
    @SerializedName("quickActions") val quickActions: List<QuickActionResponse>? = emptyList()
)

data class TodayMoodResponse(
    val mood: String? = "Neutral",
    val percentage: Int? = 0,
    val message: String? = ""
)

data class WeeklyMoodResponse(
    @SerializedName("day") val day: String? = "",
    @SerializedName("mood") val mood: String? = "Neutral",
    @SerializedName("percentage") val percentage: Int? = 0
)

data class QuickActionResponse(
    val title: String? = "",
    val route: String? = "",
    val icon: String? = ""
)

data class WeeklyData(
    val day: String? = "",
    val percentage: Float? = 0f
)

data class ActionItem(
    val name: String? = null,
    val route: String? = null
)

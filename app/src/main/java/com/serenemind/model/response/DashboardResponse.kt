package com.serenemind.model.response

import com.google.gson.annotations.SerializedName
import com.serenemind.model.entity.enums.MoodType
import java.time.DayOfWeek

data class DashboardResponse(
    @SerializedName("username") val username: String,
    @SerializedName("todayMood") val todayMood: String,
    @SerializedName("greeting") val greeting: String,
    @SerializedName("date") val date: String,
    @SerializedName("weeklyOverview") val weeklyOverview: List<WeeklyMood>,
    @SerializedName("quickActions") val quickActions: List<QuickAction>
)

data class WeeklyMood(

    val day: DayOfWeek,

    val mood: MoodType,

    val percentage: Int

)
data class TodayMood(
    val mood: String,
    val percentage: Int,
    val message: String
)

data class QuickAction(

    val title: String,

    val route: String,

    val icon: String

)
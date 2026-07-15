package com.serenemind.model.response

import com.google.gson.annotations.SerializedName
import com.serenemind.model.entity.enums.MoodType
import java.time.DayOfWeek

data class DashboardResponse(
    val username: String,
    val todayMood: TodayMoodResponse,
    val greeting: String,
    val date: String,
    val weeklyOverview: List<WeeklyMoodResponse>,
    val quickActions: List<QuickActionResponse>
)

data class TodayMoodResponse(
    val mood: String,
    val percentage: Int,
    val message: String
)

data class WeeklyMoodResponse(
    val day: String, // String to handle DayOfWeek name from backend
    val mood: String, // String to handle MoodType name
    val percentage: Int
)

data class QuickActionResponse(
    val title: String,
    val route: String,
    val icon: String
)

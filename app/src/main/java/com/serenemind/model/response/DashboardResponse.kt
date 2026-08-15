package com.serenemind.model.response

import com.serenemind.model.entity.enums.MoodType
import java.time.DayOfWeek

data class DashboardResponse(
    val greeting: String? = null,
    val formattedDate: String? = null,
    val todayMood: TodayMoodDto? = null,
    val weeklyOverview: List<WeeklyDayDto>? = null,
    val unreadNotificationCount: Int = 0,
    val unreadNotification: Boolean = false
)

data class TodayMoodDto(
    val hasLogged: Boolean = false,
    val mood: MoodType? = null,
    val percentage: Int? = null,
    val message: String? = null
)

data class WeeklyDayDto(
    val day: DayOfWeek? = null,
    val dayLabel: String? = null,
    val score: Int? = null,
    val mood: MoodType? = null,
    val hasData: Boolean = false
)

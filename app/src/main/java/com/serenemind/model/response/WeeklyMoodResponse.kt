package com.serenemind.model.response

import com.serenemind.model.entity.enums.MoodType
import java.time.DayOfWeek

data class WeeklyMoodResponse(
    val day: DayOfWeek? = null,
    val mood: MoodType? = null,
    val percentage: Int? = null,
    val intensity: Int? = null,
    val note: String? = null,
    val totalEntries: Int? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    
    // Legacy support for older dashboard if needed
    val dailyMoods: Map<String, Int>? = null,
    val averageIntensity: Double? = null
)

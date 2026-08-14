package com.serenemind.model.response

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.serenemind.model.entity.enums.MoodType
import com.serenemind.network.DayOfWeekAdapter
import com.serenemind.network.LocalDateAdapter
import java.time.DayOfWeek
import java.time.LocalDate

data class WeeklyMoodResponse(
    @JsonAdapter(DayOfWeekAdapter::class)
    val day: DayOfWeek? = null,
    val mood: MoodType? = null,
    val percentage: Int? = null,
    val intensity: Int? = null,
    val note: String? = null,

    // Summary Fields
    val totalEntries: Int? = null,
    @JsonAdapter(LocalDateAdapter::class)
    val startDate: LocalDate? = null,
    @JsonAdapter(LocalDateAdapter::class)
    val endDate: LocalDate? = null,

    // Computed Fields
    val dailyEntry: Boolean = false,
    val dateRange: String? = null,
    val dayName: String? = null,
    val moodEmoji: String? = null,
    val moodMessage: String? = null,
    val summary: Boolean = false,
    
    // Legacy support (optional, can be removed if not used)
    @SerializedName("value") val value: Int? = null,
    val dailyMoods: Map<String, Int>? = null,
    val averageIntensity: Double? = null
)

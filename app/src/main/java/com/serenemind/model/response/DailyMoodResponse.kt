package com.serenemind.model.response

import com.google.gson.annotations.JsonAdapter
import com.serenemind.model.entity.enums.MoodType
import com.serenemind.network.LocalDateAdapter
import java.time.LocalDate

data class DailyMoodResponse(
    @JsonAdapter(LocalDateAdapter::class)
    val date: LocalDate,
    val mood: MoodType,
    val intensity: Int,
    val score: Int? = null,
    val note: String? = null
)

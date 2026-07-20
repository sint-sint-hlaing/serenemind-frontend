package com.serenemind.model.response

import com.serenemind.model.entity.enums.MoodType

data class MoodEntryDto(
    val id: Long,
    val mood: MoodType,
    val intensity: Int,
    val note: String?,
    val date: String
)

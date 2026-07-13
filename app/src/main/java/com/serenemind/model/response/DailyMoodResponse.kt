package com.serenemind.model.response

import com.serenemind.model.entity.enums.MoodType

data class DailyMoodResponse(
    val date: String,
    val mood: MoodType,
    val intensity: Int,
    val note: String?
) {

}
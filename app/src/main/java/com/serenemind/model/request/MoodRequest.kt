package com.serenemind.model.request

import com.serenemind.model.entity.enums.MoodType

data class MoodRequest(
    val mood: MoodType,
    val intensity:Int,
    val note: String
)

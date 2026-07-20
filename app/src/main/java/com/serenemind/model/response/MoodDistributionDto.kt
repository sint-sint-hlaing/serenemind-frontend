package com.serenemind.model.response

data class MoodDistributionDto(
    val mood: String,
    val count: Int,
    val percentage: Double
)

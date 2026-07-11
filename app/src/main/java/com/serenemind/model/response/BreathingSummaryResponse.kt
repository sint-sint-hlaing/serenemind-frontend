package com.serenemind.model.response

data class BreathingSummaryResponse(
    val duration: String,
    val rounds: Int,
    val totalBreaths: Int
)

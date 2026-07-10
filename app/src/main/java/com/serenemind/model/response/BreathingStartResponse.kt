package com.serenemind.model.response

data class BreathingStartResponse(
    val sessionId: String,
    val exerciseType: String,
    val totalDurationSeconds: Int,
    val estimatedRounds: Int
)

package com.serenemind.model.request

data class MeditationSessionRequest(
    val meditationId: Long,
    val durationMinutes: Int
)

package com.serenemind.model.request

import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
data class MeditationHistoryRequest(
    var completed: Boolean = false,
    val title: String,
    val duration: Int,
    val completedAt: String, // Use String to avoid Instant serialization issues if not needed
)

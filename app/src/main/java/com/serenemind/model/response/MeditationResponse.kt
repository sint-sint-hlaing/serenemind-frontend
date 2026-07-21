package com.serenemind.model.response

data class MeditationResponse(
    val id: Long,
    val title: String,
    val duration: String,
    val description: String,
    val category: String,
    val audioUrl: String,
    val imageUrl: String? = null
)

data class MeditationCategory(
    val name: String,
    val emoji: String
)

data class MeditationDashboardResponse(
    val featured: MeditationResponse,
    val categories: List<MeditationCategory>,
    val recommended: List<MeditationResponse>
)

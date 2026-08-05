package com.serenemind.model.response

data class MeditationResponse(
    val id: Long,
    val title: String,
    val description: String,
    val category: String,
    val duration: String,
    val audioUrl: String,
    val imageUrl: String? = null,
    val favorite: Boolean = false
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

data class MeditationList(
    val id: Long,
    val title: String,
    val thumbnail: String?,
    val duration: String
)

data class FavoriteResponse(
    val favorite: Boolean,
    val message: String
)

data class ShareResponse(
    val title: String,
    val description: String,
    val imageUrl: String?,
    val shareUrl: String
)

data class TimerResponse(
    val minutes: Int,
    val message: String
)

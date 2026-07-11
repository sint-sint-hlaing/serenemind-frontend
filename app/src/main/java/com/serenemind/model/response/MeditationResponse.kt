package com.serenemind.model.response

import com.google.gson.annotations.SerializedName

data class Meditation(
    val id: Long,
    val title: String,
    val duration: String,
    val category: String,
    val audioUrl: String,
    val imageUrl: String? = null
)

data class MeditationCategory(
    val name: String,
    val emoji: String
)

data class MeditationDashboardResponse(
    val featured: Meditation,
    val categories: List<MeditationCategory>,
    val recommended: List<Meditation>
)

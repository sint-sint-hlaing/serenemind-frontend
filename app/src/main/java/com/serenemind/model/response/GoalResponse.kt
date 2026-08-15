// GoalResponse.kt
package com.serenemind.model.response

import com.google.gson.annotations.SerializedName
import com.serenemind.model.entity.enums.Frequency
import com.serenemind.model.entity.enums.GoalStatus

data class GoalResponse(
    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("frequency")
    val frequency: Frequency? = null,  // Made nullable

    @SerializedName("targetDays")
    val targetDays: Int? = null,

    @SerializedName("unit")
    val unit: String? = null,

    @SerializedName("startDate")
    val startDate: String? = null,

    @SerializedName("targetDate")
    val targetDate: String? = null,

    @SerializedName("completedAt")
    val completedAt: String? = null,

    @SerializedName("updatedAt")
    val updatedAt: String? = null,

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("icon")
    val icon: String? = null,

    @SerializedName("silentMode")
    val silentMode: Boolean? = null,

    @SerializedName("progress")
    val progress: Int? = null,
    val color:String?=null,

    @SerializedName("totalDays")
    val totalDays: Int? = null,

    @SerializedName("streak")
    val streak: Int? = null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("history")
    val history: List<ProgressHistory>? = null,

    @SerializedName("notes")
    val notes: List<GoalNoteDTO>? = null
)

// ✅ Extension function - defined outside the class
fun GoalResponse.toUserGoal(): UserGoal {
    return UserGoal(
        id = this.id ?: 0,
        title = this.title ?: "Untitled",
        description = this.description,
        targetDays = this.targetDays ?: 0,
        progress = this.progress ?: 0,
        streak = this.streak ?: 0,
        status = this.status?.let {
            try {
                GoalStatus.valueOf(it)
            } catch (e: Exception) {
                GoalStatus.ACTIVE
            }
        } ?: GoalStatus.ACTIVE,
        color = this.color,
        icon = this.icon,
        history = this.history?.map {
            ProgressHistoryItem(
                date = it.date ?: "",
                completed = it.completed ?: false,
                notes = it.notes,
                value = it.value ?: 0.0
            )
        },
        notes = this.notes?.map { it.content ?: "" }
    )
}
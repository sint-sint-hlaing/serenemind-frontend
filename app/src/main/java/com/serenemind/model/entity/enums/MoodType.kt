// MoodType.kt
package com.serenemind.model.entity.enums

enum class MoodType(
    val emoji: String,
    val message: String,
    val color: String
) {
    HAPPY("😊", "Feeling great!", "#4CAF50"),
    CALM("😌", "Peaceful and relaxed", "#2196F3"),
    NEUTRAL("😐", "Feeling okay", "#9E9E9E"),
    SAD("☹️", "Feeling down", "#FF5722"),
    ANXIOUS("😰", "Feeling anxious", "#FF9800"),
    ANGRY("😡", "Feeling frustrated", "#F44336")
}
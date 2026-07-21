package com.serenemind.model.entity.enums

enum class MoodType(val emoji: String, val percentage: Int, val message: String) {
    HAPPY("😊", 90, "Keep smiling today"),
    CALM("😌", 85, "Stay peaceful and relaxed"),
    NEUTRAL("🌱", 60, "Today is a fresh start"),
    SAD("💙", 40, "Take care of yourself"),
    ANXIOUS("🌿", 35, "Take a deep breath"),
    ANGRY("❤️", 20, "Relax and stay calm")
}

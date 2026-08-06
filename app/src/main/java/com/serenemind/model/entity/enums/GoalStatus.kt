package com.serenemind.model.entity.enums

enum class GoalStatus(val displayName: String, val description: String) {
    ACTIVE("Active", "Goal is in progress"),
    COMPLETED("Completed", "Goal has been completed successfully"),
    PAUSED("Paused", "Goal is temporarily paused"),
    CANCELLED("Cancelled", "Goal has been cancelled"),
    EXPIRED("Expired", "Goal has expired without completion"),
    ARCHIVED("Archived", "Goal has been archived");

    fun isActive(): Boolean = this == ACTIVE || this == PAUSED
    fun isFinished(): Boolean = this == COMPLETED || this == CANCELLED || this == EXPIRED
}

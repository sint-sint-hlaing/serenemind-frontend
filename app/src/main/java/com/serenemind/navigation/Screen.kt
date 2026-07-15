package com.serenemind.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Attribution
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String = "",
    val icon: ImageVector? = null
) {
    // Auth
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object Register : Screen("register")

    // Main container
    object Main : Screen("main")

    // Bottom Tabs
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Journal : Screen("journal", "Journal", Icons.Default.DateRange)
    object Mood : Screen("mood", "Mood", Icons.Default.Face)
    object MoodHistory : Screen("mood_history")
    object Goal: Screen(route="goal", title="Goal", icon = Icons.Default.Attribution)
    object GoalDetail: Screen("goal_detail")
    object Community : Screen("community", "Community", icon = Icons.Default.People)
    object Streak : Screen("streak", "Streak", Icons.Default.Share) // Temporary icon
    object Goal: Screen(route="goal",title="Goal", Icons.Default.Attribution)
    object Community : Screen("community", "Community", Icons.Default.People)
    object Notifications : Screen("notifications")
    object CreatePost : Screen("create_post")
    object Reminders : Screen("reminders")
    object AddReminder : Screen("add_reminder")
    object Breathing : Screen("breathing")
    object Meditation: Screen("meditation")
    object Profile : Screen("profile", "Profile", icon = Icons.Default.Person)
}

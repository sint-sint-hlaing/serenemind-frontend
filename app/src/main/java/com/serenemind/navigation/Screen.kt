package com.serenemind.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
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
    object Journal : Screen("journal", "Journal", Icons.AutoMirrored.Filled.Assignment)
    object Mood : Screen("mood", "Mood", Icons.Default.Face)
    object Streak : Screen("streak", "Streak", Icons.Default.Whatshot)
    object Goal : Screen("goal", "Goal", Icons.Default.Flag)
    object Community : Screen("community", "Community", Icons.Default.Groups)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)

    // Sub Screens
    object MoodHistory : Screen("mood_history")
    object GoalDetail : Screen("goal_detail")
    object Notifications : Screen("notifications")
    object CreatePost : Screen("create_post")
    object Reminders : Screen("reminders")
    object AddReminder : Screen("add_reminder")
    object Breathing : Screen("breathing")
    object Meditation : Screen("meditation")
}

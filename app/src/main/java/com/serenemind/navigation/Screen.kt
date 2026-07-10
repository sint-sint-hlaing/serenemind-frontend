package com.serenemind.navigation


import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share



import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Attribution
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
    object Goal: Screen(route="goal",title="Goal", Icons.Default.Attribution)
    object Community : Screen("community", "Community", Icons.Default.People)
    object CreatePost : Screen("create_post")
    object Reminders : Screen("reminders")
    object AddReminder : Screen("add_reminder")
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
}
package com.serenemind.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.serenemind.ui.theme.*

@Composable
fun BottomBar(navController: NavHostController) {
    val items = listOf(
        Screen.Home,
        Screen.Journal,
        Screen.Mood,
        Screen.Community,
        Screen.Profile
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { screen ->
            val isSelected = currentRoute == screen.route || 
                (screen == Screen.Mood && currentRoute == Screen.MoodHistory.route) ||
                (screen == Screen.Goal && currentRoute == Screen.GoalDetail.route)

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { 
                    Icon(
                        imageVector = screen.icon!!, 
                        contentDescription = screen.title,
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else TextSecondary
                    ) 
                },
                label = { 
                    Text(
                        text = screen.title,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else TextSecondary
                    ) 
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = ActionMeditation.copy(alpha = 0.5f)
                )
            )
        }
    }
}

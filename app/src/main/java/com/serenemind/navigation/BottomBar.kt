package com.serenemind.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { screen ->
            val isConceptuallySelected = currentRoute == screen.route ||
                (screen == Screen.Mood && currentRoute == Screen.MoodHistory.route) ||
                (screen == Screen.Home && (
                    currentRoute == Screen.Goal.route ||
                    currentRoute == Screen.GoalDetail.route ||
                    currentRoute == Screen.Meditation.route ||
                    currentRoute == Screen.Breathing.route ||
                    currentRoute == Screen.Notifications.route
                ))

            val isExactlyOnRoute = currentRoute == screen.route
                (screen == Screen.Goal && (currentRoute == screen.route || currentRoute == Screen.GoalDetail.route))

            NavigationBarItem(
                selected = isConceptuallySelected,
                onClick = {
                    if (isExactlyOnRoute) {
                        // Refresh logic could go here, e.g. notify current screen
                        // For now, we just navigate to it again to ensure LaunchedEffect(Unit) might re-run
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    } else {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = false // Set to false to prevent potential restoration crashes
                        }
                    }
                },
                icon = {
                    screen.icon?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = screen.title
                        )
                    }
                },
                label = {
                    Text(
                        text = screen.title,
                        fontWeight = if (isConceptuallySelected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = TextSecondary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = ActionMeditation.copy(alpha = 0.5f)
                )
            )
        }
    }
}

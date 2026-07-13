package com.serenemind.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomBar(navController: NavHostController) {
    val items = listOf(
        Screen.Home,
        Screen.Journal,
        Screen.Mood,
        Screen.Community,
        Screen.Profile
    )

    NavigationBar() {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                // We don't save state here to ensure that switching tabs 
                                // always brings the user back to the root of that tab.
                                saveState = false 
                            }
                            launchSingleTop = true
                            restoreState = false
                        }
                    }
                },
                icon = { Icon(screen.icon!!, contentDescription = screen.title) },
                label = { Text(screen.title) }
            )
        }
    }
}
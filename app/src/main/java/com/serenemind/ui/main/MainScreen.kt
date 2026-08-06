package com.serenemind.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.serenemind.navigation.BottomBar
import com.serenemind.navigation.BottomNavGraph

@Composable
fun MainScreen(
    isDarkMode: Boolean,
    onDarkModeToggle: (Boolean) -> Unit,
    onLogout: () -> Unit = {}
) {

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Routes where BottomBar should be hidden
    val hideBottomBar = currentRoute?.startsWith("chat") == true || 
                       currentRoute?.startsWith("post_detail") == true

    Scaffold(
        bottomBar = {
            if (!hideBottomBar) {
                BottomBar(navController)
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(if (hideBottomBar) PaddingValues(0.dp) else padding)) {
            BottomNavGraph(
                navController = navController,
                isDarkMode = isDarkMode,
                onDarkModeToggle = onDarkModeToggle,
                onLogout = onLogout
            )
        }
    }
}
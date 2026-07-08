package com.serenemind.ui.main

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.serenemind.navigation.BottomBar
import com.serenemind.navigation.BottomNavGraph

@Composable
fun MainScreen(
    onLogout: () -> Unit = {}
) {

    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomBar(navController)
        }
    ) { padding ->

        BottomNavGraph(
            navController = navController,
            onLogout = onLogout
        )

    }
}
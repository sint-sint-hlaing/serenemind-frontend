package com.serenemind.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
        Box(modifier = Modifier.padding(padding)) {
            BottomNavGraph(
                navController = navController,
                onLogout = onLogout
            )
        }
    }
}
package com.serenemind.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.serenemind.datastore.TokenManager
import com.serenemind.ui.login.LoginScreen
import com.serenemind.ui.main.MainScreen
import com.serenemind.ui.login.LoginViewModel

@Composable
fun AppNavigation(
    loginViewModel: LoginViewModel
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    
    // Check for existing token at startup
    val tokenState = produceState<String?>(initialValue = null) {
        value = tokenManager.getToken()
    }
    val token by tokenState
    
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            // If token is found, navigate to Dashboard
            LaunchedEffect(token) {
                if (!token.isNullOrEmpty()) {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            }

            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Main.route) {
            MainScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                }
            )
        }
    }
}

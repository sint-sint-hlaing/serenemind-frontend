package com.serenemind.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.serenemind.ui.login.LoginScreen
import com.serenemind.ui.main.MainScreen
import com.serenemind.ui.login.LoginViewModel

@Composable
fun AppNavigation(
    loginViewModel: LoginViewModel
) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {

        composable(Screen.Login.route) {

            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {

                    navController.navigate(Screen.Main.route) {

                        popUpTo(Screen.Login.route) {
                            inclusive = true
                        }

                    }

                }
            )
        }

        composable(Screen.Main.route) {
            MainScreen()
        }
    }
}
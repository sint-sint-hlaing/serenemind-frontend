package com.serenemind.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.serenemind.datastore.TokenManager
import com.serenemind.ui.login.LoginScreen
import com.serenemind.ui.login.RegisterScreen
import com.serenemind.ui.login.WelcomeScreen
import com.serenemind.ui.main.MainScreen
import com.serenemind.ui.login.LoginViewModel
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(
    loginViewModel: LoginViewModel,
    tokenManager: TokenManager,
    isDarkMode: Boolean,
    onDarkModeToggle: (Boolean) -> Unit
) {
    val navController = rememberNavController()
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val token = tokenManager.getToken()
        startDestination = if (token != null) Screen.Main.route else Screen.Welcome.route
    }

    if (startDestination == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        NavHost(
            navController = navController,
            startDestination = startDestination!!
        ) {
            composable(Screen.Welcome.route) {
                WelcomeScreen(
                    onGetStarted = {
                        navController.navigate(Screen.Register.route)
                    },
                    onLoginClick = {
                        navController.navigate(Screen.Login.route)
                    }
                )
            }

            composable(Screen.Login.route) {
                LoginScreen(
                    viewModel = loginViewModel,
                    onLoginSuccess = {
                        navController.navigate(Screen.Main.route) {
                            popUpTo(Screen.Welcome.route) {
                                inclusive = true
                            }
                        }
                    },
                    onRegisterClick = {
                        navController.navigate(Screen.Register.route)
                    },
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    viewModel = loginViewModel,
                    onRegisterSuccess = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Register.route) {
                                inclusive = true
                            }
                        }
                    },
                    onLoginClick = {
                        navController.navigate(Screen.Login.route)
                    },
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.Main.route) {
                val scope = rememberCoroutineScope()
                MainScreen(
                    isDarkMode = isDarkMode,
                    onDarkModeToggle = onDarkModeToggle,
                    onLogout = {
                        scope.launch {
                            tokenManager.clearTokens()
                            navController.navigate(Screen.Welcome.route) {
                                popUpTo(Screen.Main.route) {
                                    inclusive = true
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

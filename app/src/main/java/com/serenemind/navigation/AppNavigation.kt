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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.serenemind.repository.AuthRepository
import com.serenemind.ui.login.*
import com.serenemind.ui.main.MainScreen
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(
    loginViewModel: LoginViewModel,
    authRepository: AuthRepository,
    tokenManager: TokenManager,
    isDarkMode: Boolean,
    onDarkModeToggle: (Boolean) -> Unit
) {
    val navController = rememberNavController()
    var startDestination by remember { mutableStateOf<String?>(null) }
    
    val forgotPasswordViewModel: ForgotPasswordViewModel = viewModel(
        factory = ForgotPasswordViewModelFactory(authRepository)
    )

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
                            popUpTo(navController.graph.id) {
                                inclusive = true
                            }
                        }
                    },
                    onRegisterClick = {
                        navController.navigate(Screen.Register.route)
                    },
                    onForgotPasswordClick = {
                        navController.navigate(Screen.ForgotPassword.route)
                    },
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.ForgotPassword.route) {
                ForgotPasswordScreen(
                    viewModel = forgotPasswordViewModel,
                    onSuccess = {
                        navController.navigate(Screen.ResetPassword.route)
                    },
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.ResetPassword.route) {
                ResetPasswordScreen(
                    viewModel = forgotPasswordViewModel,
                    onSuccess = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.ForgotPassword.route) {
                                inclusive = true
                            }
                        }
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

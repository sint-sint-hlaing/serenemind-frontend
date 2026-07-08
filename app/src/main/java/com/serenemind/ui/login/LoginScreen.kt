package com.serenemind.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val state by viewModel.uiState.collectAsState()

    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {

        when (state) {

            is LoginUiState.Loading -> {

                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is LoginUiState.Success -> {

                LaunchedEffect(Unit) {
                    onLoginSuccess()
                    viewModel.reset()
                }
            }

            else -> {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {

                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(8.dp))

                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(16.dp))

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.login(email, password)
                        }
                    ) {
                        Text("Login")
                    }
                }
            }
        }
    }
}
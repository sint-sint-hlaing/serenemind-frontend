package com.serenemind

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.serenemind.datastore.TokenManager
import com.serenemind.navigation.AppNavigation
import com.serenemind.network.NetworkModule
import com.serenemind.repository.AuthRepository
import com.serenemind.ui.login.LoginViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val api = NetworkModule.provideApiService(this)
        val repo = AuthRepository(api)
        val tokenManager = TokenManager(this)

        val viewModel = LoginViewModel(repo, tokenManager)

        setContent {
            AppNavigation(viewModel)
        }
    }
}
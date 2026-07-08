package com.serenemind.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.serenemind.datastore.TokenManager
import com.serenemind.network.NetworkModule
import com.serenemind.repository.UserRepository
import com.serenemind.ui.home.HomeScreen
import com.serenemind.ui.profile.ProfileScreen
import com.serenemind.ui.profile.ProfileViewModel
import com.serenemind.ui.profile.ProfileViewModelFactory

@Composable
fun BottomNavGraph(
    navController: NavHostController,
    onLogout: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(onLogout = onLogout)
        }

        composable(Screen.Journal.route) {
            SampleScreen(title = "Journal Screen")
        }

        composable(Screen.Mood.route) {
            SampleScreen(title = "Mood Screen")
        }

        composable(Screen.Community.route) {
            SampleScreen(title = "Community Screen")
        }

        composable(Screen.Profile.route) {
            val context = LocalContext.current

            // 1. TokenManager ကို ဆောက်ပါ
            val tokenManager = remember {
                TokenManager(context)
            }

            // ပြင်ဆင်ရန်- NetworkModule ကြီးတစ်ခုလုံး မဟုတ်ဘဲ provideApiService() function ကို ခေါ်ယူပါ
            val apiService = remember {
                NetworkModule.provideApiService()
            }

            // 2. ရလာတဲ့ ApiService ကို UserRepository ထဲ ထည့်ပေးပါ
            val userRepository = remember {
                UserRepository(apiService, tokenManager)
            }

            // 3. Factory သုံးပြီး ViewModel Instance ကို ဆောက်ပါ
            val profileViewModel: ProfileViewModel = viewModel(
                factory = ProfileViewModelFactory(userRepository)
            )

            // 4. Screen ဆီကို ViewModel ထည့်ပေးလိုက်ပါ
            ProfileScreen(
                viewModel = profileViewModel,
                onNavigateToSettings = { }
            )
        }
    }
}

@Composable
fun SampleScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = title)
    }
}
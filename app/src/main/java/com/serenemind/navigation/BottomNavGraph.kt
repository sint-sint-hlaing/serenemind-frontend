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
import com.serenemind.repository.CommunityRepository
import com.serenemind.repository.DashboardRepository
import com.serenemind.repository.UserRepository
import com.serenemind.ui.community.CommunityScreen
import com.serenemind.ui.community.CommunityViewModel
import com.serenemind.ui.community.CommunityViewModelFactory
import com.serenemind.ui.community.PostDetailScreen
import com.serenemind.ui.community.PostDetailViewModel
import com.serenemind.ui.community.PostDetailViewModelFactory
import com.serenemind.ui.home.HomeScreen
import com.serenemind.ui.home.HomeViewModel
import com.serenemind.ui.home.HomeViewModelFactory
import com.serenemind.ui.profile.ProfileScreen
import com.serenemind.ui.profile.ProfileViewModel
import com.serenemind.ui.profile.ProfileViewModelFactory

@Composable
fun BottomNavGraph(
    navController: NavHostController,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val apiService = remember { NetworkModule.provideApiService() }

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            val dashboardRepository = remember {
                DashboardRepository(apiService, tokenManager)
            }
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModelFactory(dashboardRepository)
            )

            HomeScreen(
                viewModel = homeViewModel,
                onLogout = onLogout
            )
        }

        composable(Screen.Journal.route) {
            SampleScreen(title = "Journal Screen")
        }

        composable(Screen.Mood.route) {
            SampleScreen(title = "Mood Screen")
        }

        composable(Screen.Community.route) {
            val communityRepository = remember {
                CommunityRepository(apiService, tokenManager)
            }
            val communityViewModel: CommunityViewModel = viewModel(
                factory = CommunityViewModelFactory(communityRepository)
            )
            CommunityScreen(
                viewModel = communityViewModel,
                onPostClick = { post ->
                    navController.navigate("post_detail/${post.id}")
                }
            )
        }

        composable("post_detail/{postId}") { backStackEntry ->
            val postIdStr = backStackEntry.arguments?.getString("postId")
            val postId = postIdStr?.toLongOrNull() ?: -1L
            
            val communityRepository = remember {
                CommunityRepository(apiService, tokenManager)
            }
            val postDetailViewModel: PostDetailViewModel = viewModel(
                factory = PostDetailViewModelFactory(communityRepository, postId)
            )
            
            PostDetailScreen(
                viewModel = postDetailViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Profile.route) {
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
                onNavigateToSettings = { },
                onLogout = onLogout
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

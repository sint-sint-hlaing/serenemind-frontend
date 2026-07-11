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
import com.serenemind.network.ApiClient.apiService
import com.serenemind.network.NetworkModule
import com.serenemind.repository.BreathingRepository
import com.serenemind.repository.CommunityRepository
import com.serenemind.repository.DashboardRepository
import com.serenemind.repository.ReminderRepository
import com.serenemind.repository.UserRepository
import com.serenemind.ui.breathing.BreathingScreen
import com.serenemind.ui.breathing.BreathingViewModel
import com.serenemind.ui.breathing.BreathingViewModelFactory
import com.serenemind.ui.community.CommunityScreen
import com.serenemind.ui.community.CommunityViewModel
import com.serenemind.ui.community.CommunityViewModelFactory
import com.serenemind.ui.community.CreatePostScreen
import com.serenemind.ui.community.CreatePostViewModel
import com.serenemind.ui.community.CreatePostViewModelFactory
import com.serenemind.ui.community.PostDetailScreen
import com.serenemind.ui.community.PostDetailViewModel
import com.serenemind.ui.community.PostDetailViewModelFactory
import com.serenemind.ui.home.HomeScreen
import com.serenemind.ui.home.HomeViewModel
import com.serenemind.ui.home.HomeViewModelFactory
import com.serenemind.ui.profile.AddReminderScreen
import com.serenemind.ui.profile.ProfileScreen
import com.serenemind.ui.profile.ProfileViewModel
import com.serenemind.ui.profile.ProfileViewModelFactory
import com.serenemind.ui.profile.ReminderViewModel
import com.serenemind.ui.profile.ReminderViewModelFactory
import com.serenemind.ui.profile.RemindersScreen
import com.serenemind.repository.*
import com.serenemind.ui.home.*
import com.serenemind.ui.profile.*
import com.serenemind.ui.mood.*
import com.serenemind.ui.goal.*
import com.serenemind.ui.meditation.*
import com.serenemind.ui.breathing.*

@Composable
fun BottomNavGraph(
    navController: NavHostController,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    val communityRepository = remember {
        CommunityRepository(apiService, tokenManager)
    }
    val communityViewModel: CommunityViewModel = viewModel(
        factory = CommunityViewModelFactory(communityRepository)
    )
    val apiService = remember { NetworkModule.provideApiService(context) }
    val goalApiService = remember { NetworkModule.provideGoalApiService(context) }
    val meditationApiService = remember { NetworkModule.provideMeditationApiService(context) }

    // Repositories
    val goalRepository = remember { GoalRepository(goalApiService) }
    val meditationRepository = remember { MeditationRepository(meditationApiService) }
    val moodRepository = remember { MoodRepository(apiService) }

    // Shared ViewModels (for nested navigation if needed) or unique per destination
    val goalViewModel: GoalViewModel = viewModel(factory = GoalViewModelFactory(goalRepository))

    val userRepository = remember {
        UserRepository(apiService, tokenManager)
    }
    val profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(userRepository)
    )

    val reminderRepository = remember {
        ReminderRepository(apiService, tokenManager)
    }
    val reminderViewModel: ReminderViewModel = viewModel(
        factory = ReminderViewModelFactory(reminderRepository)
    )

    val breathingRepository = remember {
        BreathingRepository(apiService, tokenManager)
    }
    val breathingViewModel: BreathingViewModel = viewModel(
        factory = BreathingViewModelFactory(breathingRepository)
    )

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            val dashboardRepository = remember { DashboardRepository(apiService, tokenManager) }
            val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(dashboardRepository))
            HomeScreen(
                viewModel = homeViewModel,
                onLogout = onLogout,
                onNavigateToBreathing = {
                    navController.navigate(Screen.Breathing.route)
                }
            )
        }

        composable(Screen.Breathing.route) {
            BreathingScreen(
                viewModel = breathingViewModel,
                onBack = { navController.popBackStack() }
            )
                onLogout = onLogout
            ) { action ->
                when (action.lowercase()) {
                    "meditate" -> navController.navigate(Screen.Meditation.route)
                    "goals" -> navController.navigate(Screen.Goal.route)
                    "journal" -> navController.navigate(Screen.Journal.route)
                    "mood" -> navController.navigate(Screen.Mood.route)
                    "breathing" -> navController.navigate(Screen.Breathing.route)
                }
            }
        }

        composable(Screen.Breathing.route) {
            BreathingScreen(
                onBack = { navController.popBackStack() },
                viewModel = TODO()
            )
        }

        composable(Screen.Journal.route) {
            SampleScreen("Journal")
        }


        composable(Screen.Mood.route) {
            val moodViewModel: MoodViewModel = viewModel(factory = MoodViewModelFactory(moodRepository))
            MoodTrackerScreen(
                viewModel = moodViewModel,
                onBack = { navController.popBackStack() },
                onViewHistory = { navController.navigate(Screen.MoodHistory.route) }
            )
        }

        composable(Screen.MoodHistory.route) {
            val moodViewModel: MoodViewModel = viewModel(factory = MoodViewModelFactory(moodRepository))
            MoodHistoryScreen(
                viewModel = moodViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Goal.route) {
            GoalScreen(
                viewModel = goalViewModel,
                onGoalClick = { goal ->
                    goalViewModel.selectGoal(goal)
                    navController.navigate(Screen.GoalDetail.route)
                }
            )
        }

        composable(Screen.GoalDetail.route) {
            GoalDetailScreen(
                viewModel = goalViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Meditation.route) {
            val meditationViewModel: MeditationViewModel = viewModel(
                factory = MeditationViewModelFactory(meditationRepository)
            )
            MeditationScreen(
                viewModel = meditationViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Community.route) {
            CommunityScreen(
                viewModel = communityViewModel,
                onPostClick = { post ->
                    navController.navigate("post_detail/${post.id}")
                },
                onCreatePostClick = {
                    navController.navigate(Screen.CreatePost.route)
                }
            )
        }

        composable("post_detail/{postId}") { backStackEntry ->
            val postIdStr = backStackEntry.arguments?.getString("postId")
            val postId = postIdStr?.toLongOrNull() ?: -1L

            val postDetailViewModel: PostDetailViewModel = viewModel(
                factory = PostDetailViewModelFactory(communityRepository, postId)
            )

            PostDetailScreen(
                viewModel = postDetailViewModel,
                onBack = {
                    communityViewModel.refresh()
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.CreatePost.route) {
            val createPostViewModel: CreatePostViewModel = viewModel(
                factory = CreatePostViewModelFactory(communityRepository)
            )

            CreatePostScreen(
                viewModel = createPostViewModel,
                profileViewModel = profileViewModel,
                onBackClick = { navController.popBackStack() },
                onPostSuccess = {
                    communityViewModel.refresh()
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Profile.route) {
            val userRepository = remember { UserRepository(apiService, tokenManager) }
            val profileViewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(userRepository))
            ProfileScreen(
                viewModel = profileViewModel,
                onNavigateToSettings = { },
                onLogout = onLogout,
                onNavigateToReminders = {
                    navController.navigate(Screen.Reminders.route)
                }
            )
        }

        composable(Screen.Reminders.route) {
            RemindersScreen(
                viewModel = reminderViewModel,
                onBackClick = { navController.popBackStack() },
                onAddClick = { navController.navigate(Screen.AddReminder.route) }
            )
        }

        composable(Screen.AddReminder.route) {
            AddReminderScreen(
                viewModel = reminderViewModel,
                onBackClick = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
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

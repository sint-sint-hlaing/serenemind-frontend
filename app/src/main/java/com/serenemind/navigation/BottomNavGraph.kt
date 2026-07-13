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
import com.serenemind.repository.*
import com.serenemind.ui.breathing.*
import com.serenemind.ui.community.*
import com.serenemind.ui.home.*
import com.serenemind.ui.profile.*
import com.serenemind.ui.mood.*
import com.serenemind.ui.goal.*
import com.serenemind.ui.meditation.*
import com.serenemind.ui.journal.*

@Composable
fun BottomNavGraph(
    navController: NavHostController,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val apiService = remember { NetworkModule.provideApiService(context) }
    val goalApiService = remember { NetworkModule.provideGoalApiService(context) }
    val meditationApiService = remember { NetworkModule.provideMeditationApiService(context) }

    // Repositories
    val communityRepository = remember { CommunityRepository(apiService, tokenManager) }
    val goalRepository = remember { GoalRepository(goalApiService) }
    val meditationRepository = remember { MeditationRepository(meditationApiService) }
    val moodRepository = remember { MoodRepository(apiService) }
    val userRepository = remember { UserRepository(apiService, tokenManager) }
    val reminderRepository = remember { ReminderRepository(apiService, tokenManager) }
    val breathingRepository = remember { BreathingRepository(apiService, tokenManager) }
    val dashboardRepository = remember { DashboardRepository(apiService, tokenManager) }

    // ViewModels
    val communityViewModel: CommunityViewModel = viewModel(factory = CommunityViewModelFactory(communityRepository))
    val goalViewModel: GoalViewModel = viewModel(factory = GoalViewModelFactory(goalRepository))
    val profileViewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(userRepository))
    val reminderViewModel: ReminderViewModel = viewModel(factory = ReminderViewModelFactory(reminderRepository))
    val breathingViewModel: BreathingViewModel = viewModel(factory = BreathingViewModelFactory(breathingRepository))

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(dashboardRepository))
            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToBreathing = {
                    navController.navigate(Screen.Breathing.route)
                },
                onLogout = onLogout,
                onActionClick = { action ->
                    when (action.lowercase()) {
                        "meditate", "meditation" -> navController.navigate(Screen.Meditation.route)
                        "goals", "goal" -> navController.navigate(Screen.Goal.route)
                        "journal" -> navController.navigate(Screen.Journal.route)
                        "mood" -> navController.navigate(Screen.Mood.route)
                        "breathing" -> navController.navigate(Screen.Breathing.route)
                        "mood_history", "history" -> navController.navigate(Screen.MoodHistory.route)
                    }
                }
            )
        }

        composable(Screen.Breathing.route) {
            BreathingScreen(
                viewModel = breathingViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Journal.route) {
            JournalScreen(
                onAddClick = { /* Navigate to New Journal */ },
                onJournalClick = { /* Navigate to Detail */ }
            )
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

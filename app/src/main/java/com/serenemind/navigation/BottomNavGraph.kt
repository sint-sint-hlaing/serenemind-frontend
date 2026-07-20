package com.serenemind.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.serenemind.datastore.ThemeManager
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
import com.serenemind.ui.notification.*
import com.serenemind.ui.streak.*

@Composable
fun BottomNavGraph(
    navController: NavHostController,
    isDarkMode: Boolean,
    onDarkModeToggle: (Boolean) -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val themeManager = remember { ThemeManager(context) }

    val apiService = remember { NetworkModule.provideApiService(context, tokenManager) }
    val goalApiService = remember { NetworkModule.provideGoalApiService(context, tokenManager) }
    val meditationApiService = remember { NetworkModule.provideMeditationApiService(context, tokenManager) }

    // Repositories
    val communityRepository = remember { CommunityRepository(apiService, tokenManager) }
    val notificationRepository = remember { NotificationRepository(apiService, tokenManager) }
    val dashboardRepository = remember { DashboardRepository(apiService, tokenManager) }
    val userRepository = remember { UserRepository(apiService, tokenManager) }
    val streakRepository = remember { StreakRepository(apiService, tokenManager) }
    val reminderRepository = remember { ReminderRepository(apiService, tokenManager) }
    val breathingRepository = remember { BreathingRepository(apiService, tokenManager) }
    val moodRepository = remember { MoodRepository(apiService, tokenManager) }
    val goalRepository = remember { GoalRepository(goalApiService) }
    val meditationRepository = remember { MeditationRepository(meditationApiService) }

    // ViewModels
    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(dashboardRepository, themeManager))
    val communityViewModel: CommunityViewModel = viewModel(factory = CommunityViewModelFactory(communityRepository))
    val notificationViewModel: NotificationViewModel = viewModel(factory = NotificationViewModelFactory(notificationRepository))
    val profileViewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(userRepository))
    val streakViewModel: StreakViewModel = viewModel(factory = StreakViewModelFactory(streakRepository))
    val reminderViewModel: ReminderViewModel = viewModel(factory = ReminderViewModelFactory(reminderRepository))
    val breathingViewModel: BreathingViewModel = viewModel(factory = BreathingViewModelFactory(breathingRepository))
    val moodViewModel: MoodViewModel = viewModel(factory = MoodViewModelFactory(moodRepository))
    val goalViewModel: GoalViewModel = viewModel(factory = GoalViewModelFactory(goalRepository))

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = homeViewModel,
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
                },
                onNotificationClick = {
                    navController.navigate(Screen.Notifications.route)
                },
                onMenuClick = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToBreathing = {
                    navController.navigate(Screen.Breathing.route)
                }
            )
        }

        composable(Screen.Notifications.route) {
            NotificationsScreen(
                viewModel = notificationViewModel,
                onNavigateToPost = { postId ->
                    navController.navigate("post_detail/$postId")
                },
                onNavigateToReminder = {
                    navController.navigate(Screen.Reminders.route)
                },
                onBack = { navController.popBackStack() }
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
            MoodTrackerScreen(
                viewModel = moodViewModel,
                onBack = { navController.popBackStack() },
                onViewHistory = { navController.navigate(Screen.MoodHistory.route) }
            )
        }

        composable(Screen.MoodHistory.route) {
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
                },
                onAddGoalClick = {
                    navController.navigate("add_goal")
                }
            )
        }

        composable("add_goal") {
            AddGoalScreen(
                viewModel = goalViewModel,
                onBack = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
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

        composable(Screen.Streak.route) {
            StreakScreen(
                viewModel = streakViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Community.route) {
            CommunityScreen(
                viewModel = communityViewModel,
                onPostClick = { post, focusComments ->
                    navController.navigate("post_detail/${post.id}?focusComments=$focusComments")
                },
                onCreatePostClick = {
                    navController.navigate(Screen.CreatePost.route)
                }
            )
        }

        composable(
            route = "post_detail/{postId}?focusComments={focusComments}",
            arguments = listOf(
                androidx.navigation.navArgument("focusComments") {
                    type = androidx.navigation.NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val postIdStr = backStackEntry.arguments?.getString("postId")
            val postId = postIdStr?.toLongOrNull() ?: -1L
            val focusComments = backStackEntry.arguments?.getBoolean("focusComments") ?: false

            val postDetailViewModel: PostDetailViewModel = viewModel(
                factory = PostDetailViewModelFactory(communityRepository, postId)
            )

            PostDetailScreen(
                viewModel = postDetailViewModel,
                focusComments = focusComments,
                onBack = {
                    communityViewModel.refresh()
                    homeViewModel.fetchDashboardData(isSilent = true)
                    streakViewModel.fetchStreak(isSilent = true)
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
                    homeViewModel.fetchDashboardData(isSilent = true)
                    streakViewModel.fetchStreak(isSilent = true)
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                viewModel = profileViewModel,
                isDarkMode = isDarkMode,
                onDarkModeToggle = onDarkModeToggle,
                onNavigateToSettings = { },
                onLogout = onLogout,
                onNavigateToReminders = {
                    navController.navigate(Screen.Reminders.route)
                },
                onNavigateToStreak = {
                    navController.navigate(Screen.Streak.route)
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

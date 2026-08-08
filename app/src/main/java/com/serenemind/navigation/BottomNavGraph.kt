package com.serenemind.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.NavType
import androidx.navigation.compose.composable

import androidx.navigation.navArgument
import com.serenemind.datastore.TokenManager
import com.serenemind.network.NetworkModule
import com.serenemind.repository.CommunityRepository
import com.serenemind.repository.DashboardRepository

import com.serenemind.repository.JournalRepository

import com.serenemind.repository.UserRepository
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

import com.serenemind.ui.notification.NotificationViewModel
import com.serenemind.ui.notification.NotificationViewModelFactory
import com.serenemind.ui.notification.NotificationsScreen

import com.serenemind.ui.journal.*

import com.serenemind.ui.profile.ProfileScreen
import com.serenemind.ui.profile.ProfileViewModel
import com.serenemind.ui.profile.ProfileViewModelFactory

import com.serenemind.datastore.ThemeManager

import com.serenemind.repository.*
import com.serenemind.ui.community.*
import com.serenemind.ui.home.*
import com.serenemind.ui.profile.*
import com.serenemind.ui.mood.*
import com.serenemind.ui.goal.*
import com.serenemind.ui.meditation.*
import com.serenemind.ui.journal.*
import com.serenemind.ui.notification.*
import com.serenemind.ui.chat.*
import com.serenemind.ui.focus.*
import com.serenemind.ui.insights.*


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
    val journalApiService = remember { NetworkModule.provideJournalApiService(context, tokenManager) }
    val chatApiService = remember { NetworkModule.provideChatApiService(context, tokenManager) }

    // Repositories
    val communityRepository = remember { CommunityRepository(apiService, tokenManager) }
    val notificationRepository = remember { NotificationRepository(apiService, tokenManager) }
    val dashboardRepository = remember { DashboardRepository(apiService, tokenManager) }
    val userRepository = remember { UserRepository(apiService, tokenManager) }
    val moodRepository = remember { MoodRepository(apiService) }
    val goalRepository = remember { GoalRepository(apiService) }
    val meditationRepository = remember { MeditationRepository(apiService) }
    val journalRepository = remember { JournalRepository(journalApiService) }
    val chatRepository = remember { ChatRepository(chatApiService) }
    
    // ViewModels
    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(dashboardRepository, moodRepository, themeManager))
    val communityViewModel: CommunityViewModel = viewModel(factory = CommunityViewModelFactory(communityRepository))
    val notificationViewModel: NotificationViewModel = viewModel(factory = NotificationViewModelFactory(notificationRepository))
    val profileViewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(userRepository))
    val moodViewModel: MoodViewModel = viewModel(factory = MoodViewModelFactory(moodRepository))
    val goalViewModel: GoalViewModel = viewModel(factory = GoalViewModelFactory(goalRepository))
    val meditationViewModel: MeditationViewModel = viewModel(factory = MeditationViewModelFactory(meditationRepository))
    val focusViewModel: FocusViewModel = viewModel(factory = FocusViewModelFactory())

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = homeViewModel,
                isDarkMode = isDarkMode,
                onLogout = onLogout,
                onActionClick = { action ->
                    when (action.lowercase()) {
                        "meditate", "meditation" -> navController.navigate(Screen.Meditation.route)
                        "goals", "goal" -> navController.navigate(Screen.Goal.route)
                        "journal" -> navController.navigate(Screen.Journal.route)
                        "mood" -> navController.navigate(Screen.Mood.route)
                        "focus" -> navController.navigate(Screen.Focus.route)
                        "insights" -> navController.navigate(Screen.Insights.route)
                        "mood_insights" -> navController.navigate(Screen.MoodInsights.route)
                        "mood_history", "history" -> navController.navigate(Screen.MoodHistory.route)
                        "chat_landing" -> navController.navigate(Screen.ChatLanding.route)
                    }
                },
                onNotificationClick = {
                    navController.navigate(Screen.Notifications.route)
                },
                onMenuClick = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }

        composable(Screen.Notifications.route) {
            NotificationsScreen(
                viewModel = notificationViewModel,
                isDarkMode = isDarkMode,
                onNavigateToPost = { postId ->
                    navController.navigate("post_detail/$postId")
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Journal.route) {
            val journalListViewModel: JournalListViewModel = viewModel(
                factory = JournalListViewModelFactory(journalRepository)
            )
            JournalListScreen(
                viewModel = journalListViewModel,
                isDarkMode = isDarkMode,
                onNavigateToEditor = { id ->
                    navController.navigate(Screen.JournalEditor.createRoute(id, id != null))
                },
                onNavigateToAnalysis = { id ->
                    navController.navigate(Screen.JournalAnalysis.createRoute(id))
                }
            )
        }

        composable(
            route = Screen.JournalEditor.route,
            arguments = listOf(
                navArgument("id") { type = NavType.IntType },
                navArgument("isEdit") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id")?.takeIf { it != -1 }
            val journalEditorViewModel: JournalEditorViewModel = viewModel(
                factory = JournalEditorViewModelFactory(journalRepository)
            )
            JournalEditorScreen(
                id = id,
                viewModel = journalEditorViewModel,
                isDarkMode = isDarkMode,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.JournalAnalysis.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: -1
            val journalAnalysisViewModel: JournalAnalysisViewModel = viewModel(
                factory = JournalAnalysisViewModelFactory(journalRepository)
            )
            JournalAnalysisScreen(
                id = id,
                viewModel = journalAnalysisViewModel,
                isDarkMode = isDarkMode,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Mood.route) {
            MoodTrackerScreen(
                viewModel = moodViewModel,
                isDarkMode = isDarkMode,
                onBack = { navController.popBackStack() },
                onViewHistory = { navController.navigate(Screen.MoodHistory.route) }
            )
        }

        composable(Screen.MoodHistory.route) {
            MoodHistoryScreen(
                viewModel = moodViewModel,
                isDarkMode = isDarkMode,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.MoodInsights.route) {
            MoodInsightsScreen(
                viewModel = moodViewModel,
                isDarkMode = isDarkMode,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Goal.route) {
            GoalScreen(
                viewModel = goalViewModel,
                isDarkMode = isDarkMode,
                onGoalClick = { goal ->
                    goalViewModel.selectGoal(goal)
                    navController.navigate(Screen.GoalDetail.route)
                },
                onAddGoalClick = {
                    navController.navigate(Screen.AddGoal.route)
                }
            )
        }

        composable(Screen.AddGoal.route) {
            AddGoalScreen(
                viewModel = goalViewModel,
                isDarkMode = isDarkMode,
                onBack = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }

        composable(Screen.GoalDetail.route) {
            GoalDetailScreen(
                viewModel = goalViewModel,
                isDarkMode = isDarkMode,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Meditation.route) {
            MeditationScreen(
                viewModel = meditationViewModel,
                isDarkMode = isDarkMode,
                onBack = { navController.popBackStack() },
                onMeditationClick = { meditation ->
                    meditationViewModel.selectMeditation(meditation)
                    navController.navigate("meditation_player")
                }
            )
        }

        composable("meditation_player") {
            MeditationPlayerScreen(
                viewModel = meditationViewModel,
                isDarkMode = isDarkMode,
                onBack = { navController.popBackStack() },
                onNavigateToTimer = {
                    navController.navigate(Screen.MeditationTimer.route)
                }
            )
        }

        composable(Screen.MeditationTimer.route) {
            MeditationTimerScreen(
                viewModel = meditationViewModel,
                isDarkMode = isDarkMode,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Community.route) {
            CommunityScreen(
                viewModel = communityViewModel,
                profileViewModel = profileViewModel,
                isDarkMode = isDarkMode,
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
                profileViewModel = profileViewModel,
                focusComments = focusComments,
                isDarkMode = isDarkMode,
                onBack = {
                    communityViewModel.refresh()
                    homeViewModel.fetchDashboardData(isSilent = true)
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
                isDarkMode = isDarkMode,
                onBackClick = { navController.popBackStack() },
                onPostSuccess = {
                    communityViewModel.refresh()
                    homeViewModel.fetchDashboardData(isSilent = true)
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
                onNavigateToAbout = {
                    navController.navigate(Screen.About.route)
                },
                onNavigateToSavedPosts = {
                    navController.navigate(Screen.SavedPosts.route)
                },
                onNavigateToEditProfile = {
                    navController.navigate(Screen.EditProfile.route)
                },
                onNavigateToPersonalInfo = {
                    navController.navigate(Screen.PersonalInfo.route)
                }
            )
        }

        composable(Screen.PersonalInfo.route) {
            val personalInfoViewModel: PersonalInfoViewModel = viewModel(
                factory = PersonalInfoViewModelFactory(userRepository)
            )
            PersonalInfoScreen(
                viewModel = personalInfoViewModel,
                isDarkMode = isDarkMode,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.EditProfile.route) {
            val editProfileViewModel: EditProfileViewModel = viewModel(
                factory = EditProfileViewModelFactory(userRepository)
            )
            EditProfileScreen(
                viewModel = editProfileViewModel,
                isDarkMode = isDarkMode,
                onBack = { navController.popBackStack() },
                onSuccess = {
                    profileViewModel.fetchUserProfile()
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.About.route) {
            AboutScreen(
                isDarkMode = isDarkMode,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.SavedPosts.route) {
            val savedPostsViewModel: SavedPostsViewModel = viewModel(
                factory = SavedPostsViewModelFactory(communityRepository)
            )
            SavedPostsScreen(
                viewModel = savedPostsViewModel,
                profileViewModel = profileViewModel,
                isDarkMode = isDarkMode,
                onBack = { navController.popBackStack() },
                onPostClick = { post, focusComments ->
                    navController.navigate("post_detail/${post.id}?focusComments=$focusComments")
                }
            )
        }

        composable(Screen.ChatLanding.route) {
            val chatLandingViewModel: ChatLandingViewModel = viewModel(factory = ChatLandingViewModelFactory(chatRepository))
            ChatLandingScreen(
                viewModel = chatLandingViewModel,
                isDarkMode = isDarkMode,
                onStartChat = { starter ->
                    navController.navigate("${Screen.Chat.route}?starter=$starter")
                },
                onViewConversation = { conversationId ->
                    navController.navigate("${Screen.Chat.route}?historyId=$conversationId")
                },
                onViewAll = {
                    navController.navigate(Screen.AllConversations.route)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.AllConversations.route) {
            val chatLandingViewModel: ChatLandingViewModel = viewModel(factory = ChatLandingViewModelFactory(chatRepository))
            AllConversationsScreen(
                viewModel = chatLandingViewModel,
                isDarkMode = isDarkMode,
                onBack = { navController.popBackStack() },
                onViewConversation = { conversationId ->
                    navController.navigate("${Screen.Chat.route}?historyId=$conversationId")
                }
            )
        }

        composable(
            route = "${Screen.Chat.route}?starter={starter}&historyId={historyId}",
            arguments = listOf(
                androidx.navigation.navArgument("starter") { 
                    type = androidx.navigation.NavType.StringType
                    nullable = true 
                },
                androidx.navigation.navArgument("historyId") { 
                    type = androidx.navigation.NavType.StringType
                    nullable = true 
                }
            )
        ) { backStackEntry ->
            val starter = backStackEntry.arguments?.getString("starter")
            val historyIdStr = backStackEntry.arguments?.getString("historyId")
            val historyId = historyIdStr?.toLongOrNull()
            
            val chatViewModel: ChatViewModel = viewModel(factory = ChatViewModelFactory(chatRepository))
            
            ChatScreen(
                viewModel = chatViewModel,
                initialMessage = starter,
                historyId = historyId,
                isDarkMode = isDarkMode,
                onBack = { 
                    navController.popBackStack() 
                }
            )
        }

        composable(Screen.Focus.route) {
            FocusScreen(
                viewModel = focusViewModel,
                isDarkMode = isDarkMode,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Insights.route) {
            InsightsScreen(
                isDarkMode = isDarkMode,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

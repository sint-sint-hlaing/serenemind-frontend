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
import com.serenemind.ui.community.*
import com.serenemind.ui.home.*
import com.serenemind.ui.profile.*
import com.serenemind.ui.mood.*
import com.serenemind.ui.goal.*
import com.serenemind.ui.meditation.*
import com.serenemind.ui.journal.*
import com.serenemind.ui.notification.*
import com.serenemind.ui.chat.*

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
    val chatApiService = remember { NetworkModule.provideChatApiService(context, tokenManager) }

    // Repositories
    val communityRepository = remember { CommunityRepository(apiService, tokenManager) }
    val notificationRepository = remember { NotificationRepository(apiService, tokenManager) }
    val dashboardRepository = remember { DashboardRepository(apiService, tokenManager) }
    val userRepository = remember { UserRepository(apiService, tokenManager) }
    val moodRepository = remember { MoodRepository(apiService, tokenManager) }
    val goalRepository = remember { GoalRepository(goalApiService) }
    val meditationRepository = remember { MeditationRepository(meditationApiService) }
    val chatRepository = remember { ChatRepository(chatApiService) }

    // ViewModels
    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(dashboardRepository))
    val communityViewModel: CommunityViewModel = viewModel(factory = CommunityViewModelFactory(communityRepository))
    val notificationViewModel: NotificationViewModel = viewModel(factory = NotificationViewModelFactory(notificationRepository))
    val profileViewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(userRepository))
    val moodViewModel: MoodViewModel = viewModel(factory = MoodViewModelFactory(moodRepository))
    val goalViewModel: GoalViewModel = viewModel(factory = GoalViewModelFactory(goalRepository))

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
            JournalScreen(
                isDarkMode = isDarkMode,
                onAddClick = { /* Navigate to New Journal */ },
                onJournalClick = { /* Navigate to Detail */ }
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

        composable(Screen.Goal.route) {
            GoalScreen(
                viewModel = goalViewModel,
                isDarkMode = isDarkMode,
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
                isDarkMode = isDarkMode,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Meditation.route) {
            val meditationViewModel: MeditationViewModel = viewModel(
                factory = MeditationViewModelFactory(meditationRepository)
            )
            MeditationScreen(
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
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.EditProfile.route) {
            val editProfileViewModel: EditProfileViewModel = viewModel(
                factory = EditProfileViewModelFactory(userRepository)
            )
            EditProfileScreen(
                viewModel = editProfileViewModel,
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
                onStartChat = { starter ->
                    navController.navigate("${Screen.Chat.route}?starter=$starter")
                },
                onViewConversation = { conversationId ->
                    navController.navigate("${Screen.Chat.route}?historyId=$conversationId")
                },
                onViewAll = {
                    navController.navigate(Screen.AllConversations.route)
                }
            )
        }

        composable(Screen.AllConversations.route) {
            val chatLandingViewModel: ChatLandingViewModel = viewModel(factory = ChatLandingViewModelFactory(chatRepository))
            AllConversationsScreen(
                viewModel = chatLandingViewModel,
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
                onBack = { navController.popBackStack() }
            )
        }
    }
}

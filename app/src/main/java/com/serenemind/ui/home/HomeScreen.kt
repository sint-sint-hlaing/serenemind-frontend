package com.serenemind.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.serenemind.model.entity.enums.MoodType
import com.serenemind.model.response.DashboardResponse
import com.serenemind.model.response.QuickActionResponse
import com.serenemind.model.response.WeeklyMoodResponse
import com.serenemind.ui.streak.NewBestCelebrationScreen

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onLogout: () -> Unit = {},
    onNavigateToBreathing: () -> Unit = {},
    onNavigateToStreak: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onActionClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var showNewBest by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchDashboardData(isSilent = true)
    }

    LaunchedEffect(uiState) {
        if (uiState is HomeUiState.Success) {
            val data = (uiState as HomeUiState.Success).data
            if (viewModel.shouldShowCelebration(data.currentStreak ?: 0, data.isNewBest ?: false)) {
                showNewBest = true
                viewModel.markCelebrationShown(data.currentStreak ?: 0)
            }
        }
    }

    if (showNewBest) {
        NewBestCelebrationScreen(
            streak = (uiState as? HomeUiState.Success)?.data?.currentStreak ?: 0,
            onDismiss = { showNewBest = false }
        )
    } else {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                is HomeUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                            Text(text = "Something went wrong", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = state.message, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp, textAlign = TextAlign.Center)
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Button(
                                onClick = { viewModel.fetchDashboardData() },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                shape = RoundedCornerShape(25.dp)
                            ) {
                                Text("Retry")
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            TextButton(onClick = onLogout) {
                                Text("Logout", color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
                is HomeUiState.Success -> {
                    DashboardContent(
                        data = state.data,
                        onNavigateToBreathing = onNavigateToBreathing,
                        onNavigateToStreak = onNavigateToStreak,
                        onNavigateToNotifications = onNavigateToNotifications,
                        onActionClick = onActionClick
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardContent(
    data: DashboardResponse,
    onNavigateToBreathing: () -> Unit,
    onNavigateToStreak: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onActionClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* Open drawer */ }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu"
                )
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onNavigateToStreak() }
            ) {
                Text(
                    text = "Dashboard",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🔥", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${data.currentStreak ?: 0}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            IconButton(onClick = onNavigateToNotifications) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications"
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Greeting Section
        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            Text(
                text = if (data.date?.isNotBlank() == true) data.date else "Today",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
            Text(
                text = "${data.greeting ?: "Hello"}, ${data.username ?: "User"}! 👋",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Today's Mood Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onActionClick("mood") },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val moodType = remember(data.todayMood?.mood) {
                    MoodType.entries.find { it.name.equals(data.todayMood?.mood, ignoreCase = true) } ?: MoodType.NEUTRAL
                }
                
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = getEmojiForMood(moodType),
                        fontSize = 36.sp
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = (data.todayMood?.mood ?: "Neutral").replaceFirstChar { it.uppercase() },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = data.todayMood?.message ?: "How are you feeling?",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
                
                Text(
                    text = "${data.todayMood?.percentage ?: 0}%",
                    color = Color(0xFF4CAF50),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Weekly Overview Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Weekly Overview",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
            Text(
                text = "View all",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onActionClick("mood_history") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                WeeklyChart(data.weeklyOverview ?: emptyList())
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Quick Actions Section
        Text(
            text = "Quick Actions",
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        QuickActionsRow(
            actions = data.quickActions ?: emptyList(),
            onNavigateToBreathing = onNavigateToBreathing,
            onActionClick = onActionClick
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun WeeklyChart(weeklyOverview: List<WeeklyMoodResponse>) {
    val displayData = if (weeklyOverview.isEmpty()) {
        listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").map { WeeklyMoodResponse(it, "Neutral", 0) }
    } else {
        weeklyOverview.take(7)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        displayData.forEach { item ->
            val moodType = remember(item.mood) {
                MoodType.entries.find { it.name.equals(item.mood, ignoreCase = true) } ?: MoodType.NEUTRAL
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .width(20.dp)
                        .height(((item.percentage ?: 0).coerceAtLeast(10) * 1.0f).dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(getBarColor(moodType))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = (item.day ?: "").take(3),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = getEmojiForMood(moodType),
                    fontSize = 14.sp
                )
            }
        }
    }
}

fun getEmojiForMood(mood: MoodType): String {
    return when (mood) {
        MoodType.HAPPY -> "😊"
        MoodType.SAD -> "😢"
        MoodType.CALM -> "😌"
        MoodType.ANXIOUS -> "😰"
        MoodType.ANGRY -> "😠"
        MoodType.NEUTRAL -> "😐"
    }
}

fun getBarColor(mood: MoodType): Color {
    return when (mood) {
        MoodType.HAPPY -> Color(0xFF66BB6A)
        MoodType.CALM -> Color(0xFF29B6F6)
        MoodType.NEUTRAL -> Color(0xFFBDBDBD)
        MoodType.SAD -> Color(0xFF5C6BC0)
        MoodType.ANXIOUS -> Color(0xFFFFB74D)
        MoodType.ANGRY -> Color(0xFFEF5350)
    }
}

@Composable
fun QuickActionsRow(
    actions: List<QuickActionResponse>,
    onNavigateToBreathing: () -> Unit,
    onActionClick: (String) -> Unit
) {
    val displayActions = if (actions.isEmpty()) {
        listOf(
            QuickActionResponse("Journal", "journal", "📓"),
            QuickActionResponse("Meditate", "meditate", "🧘"),
            QuickActionResponse("Goals", "goal", "🎯"),
            QuickActionResponse("Breathing", "breathing", "🫁")
        )
    } else {
        actions.take(4)
    }

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        items(displayActions) { action: QuickActionResponse ->
            QuickActionItem(
                action = action,
                onClick = {
                    val route = action.route ?: ""
                    if (route.lowercase() == "breathing") {
                        onNavigateToBreathing()
                    } else {
                        onActionClick(route)
                    }
                }
            )
        }
    }
}

@Composable
fun QuickActionItem(
    action: QuickActionResponse,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(72.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(getBackgroundColorForAction(action.title ?: "").copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = action.icon ?: "✨",
                    fontSize = 24.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = action.title ?: "",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

fun getBackgroundColorForAction(title: String): Color {
    return when (title.lowercase()) {
        "journal" -> Color(0xFF7B1FA2)
        "meditate", "meditation" -> Color(0xFF1976D2)
        "goals" -> Color(0xFF388E3C)
        "breathing" -> Color(0xFFF57C00)
        else -> Color.Gray
    }
}

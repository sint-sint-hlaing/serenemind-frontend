package com.serenemind.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.serenemind.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToBreathing: () -> Unit = {},
    onActionClick: (String) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

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
                        Text(text = state.message, color = Color.Gray, fontSize = 14.sp, textAlign = TextAlign.Center)
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Button(
                            onClick = { viewModel.fetchDashboardData() },
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                            shape = RoundedCornerShape(27.dp)
                        ) {
                            Text("Retry")
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        TextButton(onClick = onLogout) {
                            Text("Logout & Sign In Again", color = Color.Gray)
                        }
                    }
                }
            }
            is HomeUiState.Success -> {
                DashboardContent(
                    data = state.data,
                    onNavigateToBreathing = onNavigateToBreathing,
                    onActionClick = onActionClick
                )
            }
        }
    }
}

@Composable
fun DashboardContent(
    data: DashboardResponse,
    onNavigateToBreathing: () -> Unit,
    onActionClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* Open menu */ }) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
            Text(
                text = "Dashboard",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            IconButton(onClick = { /* Notifications */ }) {
                Icon(Icons.Default.Notifications, contentDescription = "Notifications")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Greeting Section
        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            Text(
                text = data.date,
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${data.greeting}, ${data.username}! 👋",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
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
                val moodType = remember(data.todayMood.mood) {
                    MoodType.entries.find { it.name.equals(data.todayMood.mood, ignoreCase = true) } ?: MoodType.NEUTRAL
                }
                
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(getMoodBgColor(moodType)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = getEmojiForMood(moodType),
                        fontSize = 36.sp
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    val moodDisplay = data.todayMood.mood.lowercase().replaceFirstChar { it.uppercase() }
                    Text(
                        text = moodDisplay,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = data.todayMood.message,
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }
                
                Text(
                    text = "${data.todayMood.percentage}%",
                    color = Success,
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
                fontSize = 17.sp,
                color = TextPrimary
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
            Box(modifier = Modifier.padding(20.dp)) {
                WeeklyChart(data.weeklyOverview)
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Quick Actions Section
        Text(
            text = "Quick Actions",
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        QuickActionsGrid(
            actions = data.quickActions,
            onNavigateToBreathing = onNavigateToBreathing,
            onActionClick = onActionClick
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun WeeklyChart(weeklyOverview: List<WeeklyMoodResponse>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        weeklyOverview.take(7).forEach { item ->
            val moodType = remember(item.mood) {
                MoodType.entries.find { it.name.equals(item.mood, ignoreCase = true) } ?: MoodType.NEUTRAL
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .width(18.dp)
                        .height((item.percentage.coerceAtLeast(10) * 1.2f).dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(getMoodColor(moodType))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.day.take(3).lowercase().replaceFirstChar { it.uppercase() },
                    fontSize = 11.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
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

fun getMoodColor(mood: MoodType): Color {
    return when (mood) {
        MoodType.HAPPY -> MoodHappy
        MoodType.CALM -> MoodCalm
        MoodType.NEUTRAL -> MoodNeutral
        MoodType.SAD -> MoodSad
        MoodType.ANXIOUS -> MoodAnxious
        MoodType.ANGRY -> MoodAngry
    }
}

fun getMoodBgColor(mood: MoodType): Color {
    return getMoodColor(mood).copy(alpha = 0.15f)
}

@Composable
fun QuickActionsGrid(
    actions: List<QuickActionResponse>,
    onNavigateToBreathing: () -> Unit,
    onActionClick: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        actions.forEach { action ->
            QuickActionItem(
                action = action,
                onClick = {
                    val route = action.route.lowercase()
                    if (route == "breathing") {
                        onNavigateToBreathing()
                    } else {
                        onActionClick(action.route)
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
            .width(76.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.size(64.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(getBackgroundColorForAction(action.title)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = action.icon,
                    fontSize = 28.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = action.title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
    }
}

fun getBackgroundColorForAction(title: String): Color {
    return when (title.lowercase()) {
        "journal" -> ActionJournal
        "meditate", "meditation" -> ActionMeditation
        "goals" -> ActionGoals
        "breathing" -> ActionBreathing
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
}

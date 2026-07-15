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
        color = Color(0xFFFBFBFF)
    ) {
        when (val state = uiState) {
            is HomeUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF673AB7))
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
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(25.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7))
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

        Spacer(modifier = Modifier.height(20.dp))

        // Greeting Section
        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            Text(
                text = data.date,
                color = Color.Gray,
                fontSize = 13.sp
            )
            Text(
                text = "${data.greeting}, ${data.username}! 👋",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Today's Mood Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onActionClick("mood") },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
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
                        .background(Color(0xFFFFECB3)),
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
                        color = Color.Black
                    )
                    Text(
                        text = data.todayMood.message,
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }
                
                Text(
                    text = "${data.todayMood.percentage}%",
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
                color = Color(0xFF673AB7),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onActionClick("mood_history") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                WeeklyChart(data.weeklyOverview)
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
            actions = data.quickActions,
            onNavigateToBreathing = onNavigateToBreathing,
            onActionClick = onActionClick
        )

        Spacer(modifier = Modifier.height(24.dp))
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
                        .width(20.dp)
                        .height((item.percentage.coerceAtLeast(10) * 1.0f).dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(getBarColor(moodType))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.day.take(3).lowercase().replaceFirstChar { it.uppercase() },
                    fontSize = 11.sp,
                    color = Color.Gray
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
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        actions.take(4).forEach { action ->
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
            .width(72.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(getBackgroundColorForAction(action.title)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = action.icon,
                    fontSize = 24.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = action.title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
    }
}

fun getBackgroundColorForAction(title: String): Color {
    return when (title.lowercase()) {
        "journal" -> Color(0xFFF3E5F5)
        "meditate", "meditation" -> Color(0xFFE3F2FD)
        "goals" -> Color(0xFFE8F5E9)
        "breathing" -> Color(0xFFFFF3E0)
        else -> Color(0xFFF5F5F5)
    }
}

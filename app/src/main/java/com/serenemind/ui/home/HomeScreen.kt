package com.serenemind.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.serenemind.model.response.ActionItem
import com.serenemind.model.response.DashboardResponse
import com.serenemind.model.response.WeeklyData
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onLogout: () -> Unit = {},
    onActionClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFBFBFF)
    ) {
        when (val state = uiState) {
            is HomeUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is HomeUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.message, color = Color.Red)
                }
            }
            is HomeUiState.Success -> {
                DashboardContent(
                    data = state.data, 
                    onActionClick = onActionClick,
                    onMoodClick = { onActionClick("mood") }
                )
            }
        }
    }
}

@Composable
fun DashboardContent(
    data: DashboardResponse, 
    onActionClick: (String) -> Unit = {},
    onMoodClick: () -> Unit = {}
) {
    val currentDate = remember {
        val sdf = SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH)
        sdf.format(Date())
    }

    val displayActions = remember(data.quickActions) {
        val baseActions = listOf(
            ActionItem("Journal", ""),
            ActionItem("Meditate", ""),
            ActionItem("Goals", ""),
            ActionItem("Breathing", "")
        )
        
        if (data.quickActions.isNullOrEmpty()) {
            baseActions
        } else {
            val apiNames = data.quickActions.map { it.name?.lowercase() }
            val missingActions = baseActions.filter { it.name?.lowercase() !in apiNames }
            data.quickActions + missingActions
        }
    }

    val displayWeekly = if (data.weeklyOverview.isNullOrEmpty()) {
        listOf(
            WeeklyData("Mon", 60f),
            WeeklyData("Tue", 40f),
            WeeklyData("Wed", 80f),
            WeeklyData("Thu", 50f),
            WeeklyData("Fri", 65f),
            WeeklyData("Sat", 45f),
            WeeklyData("Sun", 35f)
        )
    } else {
        data.weeklyOverview
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* Open drawer */ }) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu"
                    )
                }
                Text(
                    text = "Dashboard",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { /* Notifications */ }) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications"
                    )
                }
            }

            // Greeting
            Text(
                text = currentDate,
                color = Color.Gray,
                fontSize = 12.sp
            )
            Text(
                text = "Good morning, ${data.userName ?: "User"}! 👋",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Today's Mood Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onMoodClick() },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Today's Mood",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = getEmojiForMood(data.currentMood),
                            fontSize = 48.sp
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (data.currentMood.isNullOrEmpty()) "Steady" else data.currentMood,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Great! Keep shining ☀️",
                                color = Color.Gray,
                                fontSize = 13.sp
                            )
                        }
                        Text(
                            text = "${data.moodPercentage ?: 0}%",
                            color = Color(0xFF4CAF50),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Weekly Overview Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Weekly Overview",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
                TextButton(
                    onClick = { onMoodClick() },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(text = "View all", color = Color(0xFF673AB7), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    WeeklyChart(displayWeekly)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Quick Actions Header
            Text(
                text = "Quick Actions",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            QuickActionsRow(displayActions, onActionClick)
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

fun getEmojiForMood(mood: String?): String {
    return when (mood?.lowercase()) {
        "happy" -> "😊"
        "sad" -> "☹️"
        "anxious" -> "😰"
        "angry" -> "😡"
        "calm" -> "😌"
        "neutral" -> "😐"
        else -> "😊"
    }
}

@Composable
fun WeeklyChart(weeklyOverview: List<WeeklyData>?) {
    if (weeklyOverview.isNullOrEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "No overview data available", color = Color.Gray)
        }
        return
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        weeklyOverview.forEach { dayData ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .width(16.dp)
                        .height(80.dp * ((dayData.value ?: 0f) / 100f))
                        .clip(RoundedCornerShape(8.dp))
                        .background(getBarColor(dayData.day ?: ""))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = dayData.day ?: "", fontSize = 11.sp, color = Color.Gray)
                Text(text = getEmojiForDay(dayData.day ?: ""), fontSize = 14.sp)
            }
        }
    }
}

fun getBarColor(day: String): Color {
    return when (day) {
        "Mon", "Wed" -> Color(0xFF81C784) // Green
        "Tue" -> Color(0xFFFFD54F) // Yellow/Orange
        "Thu", "Fri" -> Color(0xFFB39DDB) // Purple
        else -> Color(0xFFB0BEC5) // Blue Grey
    }
}

fun getEmojiForDay(day: String): String {
    return when (day) {
        "Mon" -> "😊"
        "Tue" -> "😐"
        "Wed" -> "😊"
        "Thu" -> "😰"
        "Fri" -> "😌"
        "Sat" -> "🟣"
        "Sun" -> "😊"
        else -> "😊"
    }
}

@Composable
fun QuickActionsRow(actions: List<ActionItem>?, onActionClick: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        actions?.take(4)?.forEach { action ->
            QuickActionItem(action, onClick = { onActionClick(action.name ?: "") })
        }
    }
}

@Composable
fun QuickActionItem(action: ActionItem, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(72.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(getBackgroundColorForAction(action.name ?: "")),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getEmojiForAction(action.name ?: ""),
                    fontSize = 22.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = action.name ?: "",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
    }
}

fun getEmojiForAction(name: String): String {
    return when (name.lowercase()) {
        "journal" -> "📓"
        "meditate" -> "🧘"
        "goals" -> "✅"
        "breathing" -> "🌬️"
        else -> "✨"
    }
}

fun getBackgroundColorForAction(name: String): Color {
    return when (name.lowercase()) {
        "journal" -> Color(0xFFF3E5F5)
        "meditate" -> Color(0xFFE3F2FD)
        "goals" -> Color(0xFFE8F5E9)
        "breathing" -> Color(0xFFFFF3E0)
        else -> Color(0xFFF5F5F5)
    }
}

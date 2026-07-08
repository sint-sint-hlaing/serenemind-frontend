package com.serenemind.ui.home

import androidx.compose.foundation.background
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.serenemind.model.response.ActionItem
import com.serenemind.model.response.DashboardResponse
import com.serenemind.model.response.WeeklyData

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
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
                    CircularProgressIndicator()
                }
            }
            is HomeUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.message, color = Color.Red)
                }
            }
            is HomeUiState.Success -> {
                DashboardContent(data = state.data)
            }
        }
    }
}

@Composable
fun DashboardContent(data: DashboardResponse) {
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
            IconButton(onClick = { /* Open drawer */ }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu"
                )
            }
            Text(
                text = "Dashboard",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { /* Notifications */ }) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications"
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Greeting
        Text(
            text = "May 12, 2024",
            color = Color.Gray,
            fontSize = 14.sp
        )
        Text(
            text = "Good morning, ${data.userName ?: "User"}! 👋",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Today's Mood Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Today's Mood",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "😊", // Large emoji
                        fontSize = 60.sp
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = data.currentMood ?: "Steady",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Great! Keep shining ☀️",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                    Text(
                        text = "${data.moodPercentage ?: 0}%",
                        color = Color(0xFF4CAF50),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Weekly Overview
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Weekly Overview",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            TextButton(onClick = { /* View all */ }) {
                Text(text = "View all", color = Color(0xFF673AB7))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        WeeklyChart(data.weeklyOverview)

        Spacer(modifier = Modifier.height(24.dp))

        // Quick Actions
        Text(
            text = "Quick Actions",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        QuickActionsRow(data.quickActions)
    }
}

@Composable
fun WeeklyChart(weeklyOverview: List<WeeklyData>?) {
    if (weeklyOverview.isNullOrEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "No overview data available", color = Color.Gray)
        }
        return
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
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
                        .width(20.dp)
                        .height(100.dp * ((dayData.value ?: 0f) / 100f))
                        .clip(RoundedCornerShape(10.dp))
                        .background(getBarColor(dayData.day ?: ""))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = dayData.day ?: "", fontSize = 12.sp, color = Color.Gray)
                Text(text = getEmojiForDay(dayData.day ?: ""), fontSize = 16.sp)
            }
        }
    }
}

fun getBarColor(day: String): Color {
    return when (day) {
        "Mon", "Wed" -> Color(0xFF81C784) // Green
        "Tue" -> Color(0xFFFFD54F) // Yellow/Orange
        else -> Color(0xFFB39DDB) // Purple
    }
}

fun getEmojiForDay(day: String): String {
    return when (day) {
        "Sat" -> "🟣" // Different emoji for Sat as in image
        else -> "😊"
    }
}

@Composable
fun QuickActionsRow(actions: List<ActionItem>?) {
    if (actions.isNullOrEmpty()) {
        Text(text = "No quick actions available", color = Color.Gray)
        return
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(actions) { action ->
            QuickActionItem(action)
        }
    }
}

@Composable
fun QuickActionItem(action: ActionItem) {
    Card(
        modifier = Modifier.width(85.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(getBackgroundColorForAction(action.name ?: "")),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getEmojiForAction(action.name ?: ""),
                    color = getIconColorForAction(action.name ?: "")
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = action.name ?: "",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
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

fun getIconColorForAction(name: String): Color {
    return when (name.lowercase()) {
        "journal" -> Color(0xFF7B1FA2)
        "meditate" -> Color(0xFF1976D2)
        "goals" -> Color(0xFF388E3C)
        "breathing" -> Color(0xFFF57C00)
        else -> Color.Gray
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    val mockData = DashboardResponse(
        userName = "Aye",
        currentMood = "Happy",
        moodPercentage = 80,
        weeklyOverview = listOf(
            WeeklyData("Mon", 60f),
            WeeklyData("Tue", 40f),
            WeeklyData("Wed", 80f),
            WeeklyData("Thu", 50f),
            WeeklyData("Fri", 65f),
            WeeklyData("Sat", 45f),
            WeeklyData("Sun", 35f)
        ),
        quickActions = listOf(
            ActionItem("Journal", ""),
            ActionItem("Meditate", ""),
            ActionItem("Goals", ""),
            ActionItem("Breathing", "")
        )
    )
    MaterialTheme {
        DashboardContent(data = mockData)
    }
}

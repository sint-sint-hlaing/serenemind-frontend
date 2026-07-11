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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.serenemind.ui.streak.NewBestCelebrationScreen

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onLogout: () -> Unit = {},
    onNavigateToBreathing: () -> Unit = {},
    onNavigateToStreak: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var showNewBest by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchDashboardData(isSilent = true)
    }

    LaunchedEffect(uiState) {
        if (uiState is HomeUiState.Success) {
            val data = (uiState as HomeUiState.Success).data
            if (viewModel.shouldShowCelebration(data.currentStreak, data.isNewBest)) {
                showNewBest = true
                viewModel.markCelebrationShown(data.currentStreak)
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
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
                is HomeUiState.Success -> {
                    DashboardContent(
                        data = state.data, 
                        onNavigateToBreathing = onNavigateToBreathing,
                        onNavigateToStreak = onNavigateToStreak
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
    onNavigateToStreak: () -> Unit
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* Open drawer */ }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onNavigateToStreak() }
            ) {
                Text(
                    text = "Dashboard",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
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
                            text = "${data.currentStreak}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            IconButton(onClick = { /* Notifications */ }) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Date and Greeting
        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            Text(
                text = "May 12, 2024",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
            Text(
                text = "Good morning, ${data.userName ?: "User"}! 👋",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Today's Mood Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Today's Mood",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "😊",
                            fontSize = 40.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = data.currentMood ?: "Happy",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Great! Keep shining ☀️",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }
                    Text(
                        text = "${data.moodPercentage ?: 0}%",
                        color = Color(0xFF4CAF50),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Weekly Overview
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Weekly Overview",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "View all",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { /* View all */ }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        WeeklyChart(data.weeklyOverview)

        Spacer(modifier = Modifier.height(32.dp))

        // Quick Actions
        Text(
            text = "Quick Actions",
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        QuickActionsRow(data.quickActions, onNavigateToBreathing)
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
            .height(160.dp)
            .padding(horizontal = 4.dp),
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
                        .width(22.dp)
                        .height(100.dp * ((dayData.value ?: 0f) / 100f))
                        .clip(RoundedCornerShape(12.dp))
                        .background(getBarColor(dayData.day ?: ""))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = dayData.day ?: "",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = getEmojiForDay(dayData.day ?: ""), fontSize = 16.sp)
            }
        }
    }
}

fun getBarColor(day: String): Color {
    return when (day) {
        "Mon", "Wed", "Sun" -> Color(0xFFA5D6A7) // Light Green
        "Tue", "Thu" -> Color(0xFFFFE082) // Light Orange/Yellow
        else -> Color(0xFFC5CAE9) // Light Blue/Purple
    }
}

fun getEmojiForDay(day: String): String {
    return when (day) {
        "Fri" -> "😊"
        "Sat" -> "😴"
        else -> "😊"
    }
}

@Composable
fun QuickActionsRow(actions: List<ActionItem>?, onNavigateToBreathing: () -> Unit) {
    val displayActions = remember(actions) {
        listOf(
            ActionItem("Journal", ""),
            ActionItem("Meditate", ""),
            ActionItem("Goals", ""),
            ActionItem("Breathing", "")
        )
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(start = 8.dp, end = 8.dp, bottom = 16.dp)
    ) {
        items(displayActions) { action ->
            QuickActionItem(action, onClick = {
                if (action.name?.lowercase() == "breathing") {
                    onNavigateToBreathing()
                }
            })
        }
    }
}

@Composable
fun QuickActionItem(action: ActionItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(85.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                    .background(getBackgroundColorForAction(action.name ?: "").copy(alpha = 0.2f)),
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
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
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
        DashboardContent(
            data = mockData, 
            onNavigateToBreathing = {},
            onNavigateToStreak = {}
        )
    }
}

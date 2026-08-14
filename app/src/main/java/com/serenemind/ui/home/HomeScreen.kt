// HomeScreen.kt
package com.serenemind.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.serenemind.model.entity.enums.MoodType
import com.serenemind.model.response.DashboardResponse
import com.serenemind.model.response.QuickActionResponse
import com.serenemind.model.response.WeeklyMoodResponse
import com.serenemind.ui.mood.getMoodColor
import com.serenemind.ui.theme.*
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    isDarkMode: Boolean,
    onLogout: () -> Unit = {},
    onActionClick: (String) -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onMenuClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchDashboardData(isSilent = true)
        viewModel.fetchWeeklyMood()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (val state = uiState) {
            is HomeUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Loading dashboard...",
                            color = if (isDarkMode) Color.LightGray else TextSecondary
                        )
                    }
                }
            }
            is HomeUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text("😌", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Something went wrong",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = if (isDarkMode) Color.White else TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.message,
                            color = if (isDarkMode) Color.LightGray else Color.Gray,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
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
                            Text("Logout & Sign In Again", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            is HomeUiState.Success -> {
                DashboardContent(
                    data = state.data,
                    isDarkMode = isDarkMode,
                    onActionClick = onActionClick,
                    onNotificationClick = onNotificationClick,
                    onMenuClick = onMenuClick
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DashboardContent(
    data: DashboardResponse,
    isDarkMode: Boolean,
    onActionClick: (String) -> Unit,
    onNotificationClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    val dateFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy")
    val formattedDate = data.date?.format(dateFormatter) ?: "Today"

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Dashboard",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNotificationClick) {
                        BadgedBox(
                            badge = {
                                if ((data.unreadNotificationCount ?: 0L) > 0) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(6.dp)
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = if (isDarkMode) Color(0xFF1A1A1A) else MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = if (isDarkMode) Color(0xFF1A1A1A) else MaterialTheme.colorScheme.background
    ) { innerPadding ->
        BoxWithConstraints(modifier = Modifier.padding(innerPadding)) {
            val isWide = maxWidth > 600.dp
            val horizontalPadding = if (isWide) 32.dp else 16.dp

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = horizontalPadding)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Header Section
                Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                    Text(
                        text = formattedDate,
                        color = if (isDarkMode) Color.LightGray else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${data.greeting ?: "Hello"}, ${data.username ?: "User"}! 🥳",
                        fontSize = if (isWide) 28.sp else 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Today's Mood Card
                TodayMoodCard(
                    data = data,
                    isDarkMode = isDarkMode,
                    onClick = { onActionClick("mood") }
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Weekly Overview Section
                WeeklyOverviewSection(
                    weeklyOverview = data.weeklyOverview ?: emptyList(),
                    isDarkMode = isDarkMode,
                    onViewAll = { onActionClick("mood_history") }
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Quick Actions Section
                QuickActionsSection(
                    actions = data.quickActions ?: emptyList(),
                    isDarkMode = isDarkMode,
                    onActionClick = onActionClick
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun TodayMoodCard(
    data: DashboardResponse,
    isDarkMode: Boolean,
    onClick: () -> Unit
) {
    val todayMood = data.todayMood
    val isMoodRecorded = todayMood?.mood != null
    val moodColor = if (isMoodRecorded) getMoodColor(todayMood!!.mood!!) else MaterialTheme.colorScheme.primary
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (isMoodRecorded) {
                        Brush.horizontalGradient(
                            colors = listOf(moodColor, moodColor.copy(alpha = 0.7f))
                        )
                    } else {
                        Brush.horizontalGradient(
                            colors = if (isDarkMode) listOf(Color(0xFF333333), Color(0xFF222222))
                            else listOf(Color(0xFFF1F8E9), Color(0xFFE8F5E9))
                        )
                    }
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isMoodRecorded) getMoodEmoji(todayMood!!.mood!!.name) else "🌱",
                        fontSize = 40.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isMoodRecorded) todayMood!!.message ?: "Feeling Steady" else "How are you feeling today?",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${todayMood?.percentage ?: 0}%",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = (todayMood?.mood?.name ?: "STEADY").uppercase(),
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun WeeklyOverviewSection(
    weeklyOverview: List<WeeklyMoodResponse>,
    isDarkMode: Boolean,
    onViewAll: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Weekly Overview",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "View all",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onViewAll() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkMode) Color(0xFF2A2A2A) else MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(modifier = Modifier.padding(20.dp)) {
                WeeklyChart(
                    weeklyOverview = weeklyOverview,
                    isDarkMode = isDarkMode
                )
            }
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuickActionsSection(
    actions: List<QuickActionResponse>,
    isDarkMode: Boolean,
    onActionClick: (String) -> Unit
) {
    Column {
        Text(
            text = "Quick Actions",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        val displayActions = if (actions.isNotEmpty()) actions else listOf(
            QuickActionResponse("Journal", "journal", "📒"),
            QuickActionResponse("Meditate", "meditation", "🧘"),
            QuickActionResponse("Goals", "goal", "🏆"),
            QuickActionResponse("Breathe", "breathe", "✌️")
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            maxItemsInEachRow = 4
        ) {
            displayActions.forEach { action ->
                QuickActionItem(
                    title = action.title ?: "",
                    icon = action.icon ?: "📖",
                    route = action.route ?: "",
                    isDarkMode = isDarkMode,
                    modifier = Modifier.weight(1f),
                    onClick = { onActionClick(action.route ?: "") }
                )
            }
        }
    }
}

@Composable
fun WeeklyChart(
    weeklyOverview: List<WeeklyMoodResponse>,
    isDarkMode: Boolean
) {
    val daysOfWeek = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
    val textColor = if (isDarkMode) Color.White else TextPrimary
    val textSecondary = if (isDarkMode) Color.LightGray else TextSecondary

    val chartData = daysOfWeek.map { dayName ->
        weeklyOverview.find { it.day?.name?.take(3) == dayName }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        chartData.forEachIndexed { index, item ->
            val mood = item?.mood
            val percentage = item?.percentage ?: 0
            val hasData = item != null && mood != null

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (hasData) getMoodEmoji(mood!!.name) else "•",
                    fontSize = 14.sp,
                    color = if (hasData) Color.Unspecified else textSecondary.copy(alpha = 0.3f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .width(18.dp)
                        .height(if (hasData) (percentage.coerceAtLeast(10) * 1.2f).dp else 30.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(
                            if (hasData) getMoodColor(mood!!)
                            else if (isDarkMode) Color(0xFF333333) else Color(0xFFF5F5F5)
                        )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = daysOfWeek[index],
                    fontSize = 10.sp,
                    color = if (hasData) textColor else textSecondary.copy(alpha = 0.6f),
                    fontWeight = if (hasData) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun QuickActionItem(
    title: String,
    icon: String,
    route: String,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val color = when {
        title.contains("Journal", ignoreCase = true) -> Color(0xFF9C27B0)
        title.contains("Meditate", ignoreCase = true) -> Color(0xFF2196F3)
        title.contains("Goals", ignoreCase = true) -> Color(0xFF4CAF50)
        title.contains("Breathe", ignoreCase = true) -> Color(0xFF7C4DFF)
        else -> Color(0xFF607D8B)
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkMode) Color(0xFF2A2A2A) else MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.size(64.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(if (isDarkMode) color.copy(alpha = 0.2f) else color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 28.sp)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

fun getMoodEmoji(mood: String): String {
    return when (mood.uppercase()) {
        "HAPPY" -> "😊"
        "CALM" -> "😌"
        "NEUTRAL" -> "😐"
        "SAD" -> "☹️"
        "ANXIOUS" -> "😰"
        "ANGRY" -> "😡"
        else -> "😐"
    }
}

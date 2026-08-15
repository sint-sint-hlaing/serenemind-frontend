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
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.serenemind.model.entity.enums.MoodType
import com.serenemind.model.response.DashboardResponse
import com.serenemind.model.response.TodayMoodDto
import com.serenemind.model.response.WeeklyDayDto
import com.serenemind.ui.theme.*

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
    }

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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardContent(
    data: DashboardResponse,
    isDarkMode: Boolean,
    onActionClick: (String) -> Unit,
    onNotificationClick: () -> Unit,
    onMenuClick: () -> Unit
) {
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
                                if (data.unreadNotification) {
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
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Greeting Section
            Text(
                text = data.formattedDate ?: "Today",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${data.greeting ?: "Hello, User"}",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Today's Mood Card
            TodayMoodCard(
                data = data.todayMood,
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
                isDarkMode = isDarkMode,
                onActionClick = onActionClick
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun TodayMoodCard(
    data: TodayMoodDto?,
    isDarkMode: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) Color(0xFF2A2A2A) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Today's Mood",
                color = if (isDarkMode) Color.LightGray else Color.Gray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val mood = data?.mood ?: MoodType.NEUTRAL
                val hasLogged = data?.hasLogged == true
                
                val emojiBgColor = when (mood) {
                    MoodType.HAPPY -> Color(0xFFFFECB3)
                    MoodType.CALM -> Color(0xFFE1F5FE)
                    MoodType.NEUTRAL -> Color(0xFFF5F5F5)
                    MoodType.SAD -> Color(0xFFE1BEE7)
                    MoodType.ANXIOUS -> Color(0xFFE0F2F1)
                    MoodType.ANGRY -> Color(0xFFFFCDD2)
                }

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(emojiBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (hasLogged) mood.emoji else "🌱",
                        fontSize = 48.sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (hasLogged) mood.name.lowercase().replaceFirstChar { it.uppercase() } else "Steady",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (hasLogged) data?.message ?: "" else "How are you feeling today?",
                        color = if (isDarkMode) Color.LightGray else Color.Gray,
                        fontSize = 13.sp
                    )
                }

                Text(
                    text = "${data?.percentage ?: 0}%",
                    color = if (hasLogged) Success else Color.Gray,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun WeeklyOverviewSection(
    weeklyOverview: List<WeeklyDayDto>,
    isDarkMode: Boolean,
    onViewAll: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
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
                WeeklyChart(weeklyOverview = weeklyOverview, isDarkMode = isDarkMode)
            }
        }
    }
}

@Composable
fun WeeklyChart(weeklyOverview: List<WeeklyDayDto>, isDarkMode: Boolean) {
    val daysLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Top
    ) {
        daysLabels.forEach { label ->
            val data = weeklyOverview.find { it.dayLabel == label }
            val score = data?.score ?: 0
            val mood = data?.mood
            val hasData = data?.hasData ?: false

            val barColor = when (label) {
                "Mon" -> Color(0xFF81C784) // Green
                "Tue" -> Color(0xFFFFD54F) // Orange
                "Wed" -> Color(0xFF64B5F6) // Blue
                "Thu" -> Color(0xFFBA68C8) // Purple
                "Fri" -> Color(0xFFF06292) // Pink
                "Sat" -> Color(0xFFFF8A65) // Coral
                "Sun" -> Color(0xFF4DB6AC) // Teal
                else -> Color(0xFF9575CD)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .height(100.dp)
                        .width(24.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    // Vertical thin line
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(0.5.dp)
                            .background(if (isDarkMode) Color.DarkGray else Color(0xFFEEEEEE))
                    )

                    // Bar starting from bottom
                    if (hasData) {
                        // Making the bar height proportional to the score (0-100)
                        val barHeight = (score.coerceIn(5, 100)).dp
                        
                        Box(
                            modifier = Modifier
                                .width(18.dp)
                                .height(barHeight)
                                .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp, bottomStart = 2.dp, bottomEnd = 2.dp))
                                .background(barColor)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = label,
                    fontSize = 11.sp,
                    color = if (isDarkMode) Color.LightGray else Color.Gray,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(if (hasData) barColor.copy(alpha = 0.15f) else Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (hasData) mood?.emoji ?: "😐" else "😐",
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun QuickActionsSection(
    isDarkMode: Boolean,
    onActionClick: (String) -> Unit
) {
    Column {
        Text(
            text = "Quick Actions",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionItem(
                title = "Journal",
                icon = Icons.AutoMirrored.Filled.Assignment,
                bgColor = Color(0xFFF3E5F5),
                iconColor = Color(0xFF7B1FA2),
                isDarkMode = isDarkMode,
                modifier = Modifier.weight(1f),
                onClick = { onActionClick("journal") }
            )
            QuickActionItem(
                title = "Meditate",
                icon = Icons.Default.SelfImprovement,
                bgColor = Color(0xFFE3F2FD),
                iconColor = Color(0xFF1976D2),
                isDarkMode = isDarkMode,
                modifier = Modifier.weight(1f),
                onClick = { onActionClick("meditation") }
            )
            QuickActionItem(
                title = "Goals",
                icon = Icons.Default.CheckCircle,
                bgColor = Color(0xFFE8F5E9),
                iconColor = Color(0xFF388E3C),
                isDarkMode = isDarkMode,
                modifier = Modifier.weight(1f),
                onClick = { onActionClick("goal") }
            )
            QuickActionItem(
                title = "SereneAI",
                icon = Icons.Default.AutoAwesome,
                bgColor = Color(0xFFFFF3E0),
                iconColor = Color(0xFFF57C00),
                isDarkMode = isDarkMode,
                modifier = Modifier.weight(1f),
                onClick = { onActionClick("chat_landing") }
            )
        }
    }
}

@Composable
fun QuickActionItem(
    title: String,
    icon: ImageVector,
    bgColor: Color,
    iconColor: Color,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkMode) Color(0xFF2A2A2A) else bgColor
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier.size(64.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (isDarkMode) iconColor.copy(alpha = 0.8f) else iconColor,
                    modifier = Modifier.size(32.dp)
                )
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

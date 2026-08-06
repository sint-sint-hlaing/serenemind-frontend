package com.serenemind.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.AutoAwesome
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
import com.serenemind.ui.theme.*

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
    val context = androidx.compose.ui.platform.LocalContext.current

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
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = onNotificationClick) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        BoxWithConstraints(modifier = Modifier.padding(innerPadding)) {
            val isWide = maxWidth > 600.dp

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = if (isWide) 32.dp else 16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Greeting Section
                Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                    Text(
                        text = data.date ?: "",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${data.greeting ?: "Hello"}, ${data.username ?: "User"}! 👋",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Today's Mood Card
                val isMoodRecorded = !data.mood.isNullOrBlank()
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(),
                            onClick = { onActionClick("mood") }
                        ),
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
                        val moodType = remember(data.mood) {
                            MoodType.entries.find { it.name.equals(data.mood, ignoreCase = true) } ?: MoodType.NEUTRAL
                        }

                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(if (isMoodRecorded) getMoodBgColor(moodType) else Color(0xFFF1F8E9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isMoodRecorded) moodType.emoji else "🌱",
                                fontSize = 36.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            val moodDisplay = if (isMoodRecorded) {
                                (data.mood ?: "").lowercase().replaceFirstChar { it.uppercase() }
                            } else {
                                "Steady"
                            }

                            Text(
                                text = moodDisplay,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            val moodTypeEnum = remember(data.mood) {
                                MoodType.entries.find { it.name.equals(data.mood, ignoreCase = true) } ?: MoodType.NEUTRAL
                            }
                            Text(
                                text = if (isMoodRecorded) moodTypeEnum.message else "How are you feeling today?",
                                color = TextSecondary,
                                fontSize = 13.sp
                            )
                        }

                        Text(
                            text = "${data.percentage ?: 0}%",
                            color = if (isMoodRecorded) Success else Color(0xFF4CAF50).copy(alpha = 0.6f),
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
                        color = MaterialTheme.colorScheme.onBackground
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
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                QuickActionsGrid(
                    actions = data.quickActions,
                    isDarkMode = isDarkMode,
                    onActionClick = onActionClick
                )

                Spacer(modifier = Modifier.height(28.dp))

                // AI Chat Entry Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onActionClick("chat_landing") },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isDarkMode) Color(0xFF333333) else Color(0xFFF0F0F0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = CircleShape,
                            color = Color(0xFFEDE7F6)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color(0xFF7E57C2)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "SereneMind AI",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Your mindful AI companion",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}


@Composable
fun WeeklyChart(weeklyOverview: List<WeeklyMoodResponse>) {
    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    // Map data to days of week for a full 7-day view
    val chartData = daysOfWeek.map { dayName ->
        weeklyOverview.find {
            it.day?.name?.take(3)?.equals(dayName, ignoreCase = true) == true
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        chartData.forEachIndexed { index, item ->
            val moodType = item?.mood
            val percentage = item?.percentage ?: 0
            val hasData = item != null

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .width(18.dp)
                        .height(if (hasData) (percentage.coerceAtLeast(10) * 1.2f).dp else 40.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(
                            if (hasData) getMoodColor(moodType!!)
                            else Color(0xFFF5F5F5)
                        )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = daysOfWeek[index],
                    fontSize = 11.sp,
                    color = if (hasData) TextPrimary else TextSecondary.copy(alpha = 0.6f),
                    fontWeight = if (hasData) FontWeight.Bold else FontWeight.Medium
                )
                Text(
                    text = if (hasData) moodType!!.emoji else "•",
                    fontSize = 14.sp,
                    color = if (hasData) Color.Unspecified else TextSecondary.copy(alpha = 0.3f)
                )
            }
        }
    }
}

fun getEmojiForMood(mood: MoodType): String = mood.emoji

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
    isDarkMode: Boolean,
    onActionClick: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        actions.forEach { action ->
            QuickActionItem(
                action = action,
                isDarkMode = isDarkMode,
                modifier = Modifier.weight(1f),
                onClick = {
                    onActionClick(action.route ?: "")
                }
            )
        }
    }
}

@Composable
fun QuickActionItem(
    action: QuickActionResponse,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = onClick
            )
            .padding(4.dp),
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
                    .background(getBackgroundColorForAction(action.title ?: "", isDarkMode)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = action.icon ?: "",
                    fontSize = 28.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = action.title ?: "",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
fun getBackgroundColorForAction(title: String, isDarkMode: Boolean): Color {
    val baseColor = when (title.lowercase()) {
        "journal" -> ActionJournal
        "meditate", "meditation" -> ActionMeditation
        "goals", "goal" -> ActionGoals
        "breathing" -> Color(0xFFFFF3E0)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    return if (isDarkMode) baseColor.copy(alpha = 0.2f) else baseColor
}

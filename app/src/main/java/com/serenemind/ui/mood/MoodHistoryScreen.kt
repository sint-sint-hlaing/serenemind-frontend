// MoodHistoryScreen.kt
package com.serenemind.ui.mood

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.serenemind.model.entity.enums.MoodType
import com.serenemind.ui.theme.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MoodHistoryScreen(
    viewModel: MoodViewModel,
    isDarkMode: Boolean,
    onBack: () -> Unit = {}
) {
    val summary by viewModel.summaryState.collectAsState()
    val history by viewModel.historyState.collectAsState()
    val selectedMood by viewModel.selectedDateMood.collectAsState()

    var calendar by remember { mutableStateOf(Calendar.getInstance()) }
    val context = LocalContext.current

    val monthYearFormat = remember { SimpleDateFormat("MMMM yyyy", Locale.ENGLISH) }
    val dayFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    // Get colors based on dark mode
    val backgroundColor = if (isDarkMode) Color(0xFF1A1A1A) else MaterialTheme.colorScheme.background
    val surfaceColor = if (isDarkMode) Color(0xFF2A2A2A) else MaterialTheme.colorScheme.surface
    val textColor = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onSurface
    val textSecondary = if (isDarkMode) Color.LightGray else MaterialTheme.colorScheme.onSurfaceVariant

    LaunchedEffect(calendar) {
        viewModel.fetchMoodHistory(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Mood History",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = textColor
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = textColor
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.refresh()
                        android.widget.Toast.makeText(context, "Refreshing mood history...", android.widget.Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = textColor
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = if (isDarkMode) Color(0xFF1A1A1A) else MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = backgroundColor
    ) { padding ->
        BoxWithConstraints(modifier = Modifier.padding(padding)) {
            val isWide = maxWidth > 600.dp

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = if (isWide) 32.dp else 20.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Calendar Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = surfaceColor
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Calendar Month Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = {
                                val newCal = calendar.clone() as Calendar
                                newCal.add(Calendar.MONTH, -1)
                                calendar = newCal
                            }) {
                                Icon(
                                    Icons.Default.ChevronLeft,
                                    contentDescription = "Prev",
                                    tint = textColor
                                )
                            }
                            Text(
                                text = monthYearFormat.format(calendar.time),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = textColor
                            )
                            IconButton(onClick = {
                                val newCal = calendar.clone() as Calendar
                                newCal.add(Calendar.MONTH, 1)
                                calendar = newCal
                            }) {
                                Icon(
                                    Icons.Default.ChevronRight,
                                    contentDescription = "Next",
                                    tint = textColor
                                )
                            }
                        }

                        // Calendar Grid Header
                        val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                        ) {
                            daysOfWeek.forEach { day ->
                                Text(
                                    text = day,
                                    color = textSecondary,
                                    fontSize = 12.sp,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Calculate days for the grid
                        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                        val firstDayOfMonth = (calendar.clone() as Calendar).apply {
                            set(Calendar.DAY_OF_MONTH, 1)
                        }
                        val firstDayOfWeek = firstDayOfMonth.get(Calendar.DAY_OF_WEEK)

                        // Convert Calendar.DAY_OF_WEEK (Sun=1, Mon=2...) to offset (Mon=0...Sun=6)
                        val offset = when(firstDayOfWeek) {
                            Calendar.MONDAY -> 0
                            Calendar.TUESDAY -> 1
                            Calendar.WEDNESDAY -> 2
                            Calendar.THURSDAY -> 3
                            Calendar.FRIDAY -> 4
                            Calendar.SATURDAY -> 5
                            Calendar.SUNDAY -> 6
                            else -> 0
                        }

                        val totalCells = daysInMonth + offset
                        val rows = (totalCells + 6) / 7

                        Column(modifier = Modifier.fillMaxWidth()) {
                            repeat(rows) { rowIndex ->
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    repeat(7) { colIndex ->
                                        val cellIndex = rowIndex * 7 + colIndex
                                        Box(modifier = Modifier.weight(1f)) {
                                            if (cellIndex >= offset && cellIndex < totalCells) {
                                                val day = cellIndex - offset + 1
                                                val cellDate = (calendar.clone() as Calendar).apply {
                                                    set(Calendar.DAY_OF_MONTH, day)
                                                }
                                                val cellLocalDate = LocalDate.of(
                                                    cellDate.get(Calendar.YEAR),
                                                    cellDate.get(Calendar.MONTH) + 1,
                                                    cellDate.get(Calendar.DAY_OF_MONTH)
                                                )
                                                val moodData = history.find { it.date == cellLocalDate }

                                                val isSelected = selectedMood?.date == cellLocalDate

                                                CalendarDayItem(
                                                    day = day,
                                                    mood = moodData?.mood,
                                                    isSelected = isSelected,
                                                    isDarkMode = isDarkMode,
                                                    onClick = { viewModel.selectDateMood(cellLocalDate) }
                                                )
                                            } else {
                                                Box(modifier = Modifier.aspectRatio(1f))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Selected Day Card
                selectedMood?.let { mood ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = surfaceColor
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = formatDateNicely(mood.date.toString()),
                                color = textSecondary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(getMoodBgColor(mood.mood)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(getEmojiForMood(mood.mood), fontSize = 36.sp)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = mood.mood.name.lowercase().replaceFirstChar { it.uppercase() },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = textColor
                                    )
                                    Text(
                                        text = "${mood.intensity}/10",
                                        color = Success,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    mood.note?.let {
                                        Text(it, color = textSecondary, fontSize = 14.sp)
                                    }
                                }
                            }
                        }
                    }
                } ?: run {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No mood recorded for this day",
                            color = textSecondary,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Mood Summary
                Text(
                    "Mood Summary (This Week)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(20.dp))

                val moodColors = mapOf(
                    "HAPPY" to MoodHappy,
                    "CALM" to MoodCalm,
                    "NEUTRAL" to MoodNeutral,
                    "SAD" to MoodSad,
                    "ANXIOUS" to MoodAnxious,
                    "ANGRY" to MoodAngry
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(130.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        MoodSummaryPieChart(summary = summary)
                    }

                    Column(
                        modifier = Modifier
                            .padding(start = 32.dp)
                            .weight(1f)
                    ) {
                        summary.forEach { (mood, percentage) ->
                            val moodKey = mood.uppercase()
                            val displayLabel = mood.lowercase().replaceFirstChar { it.uppercase() }
                            SummaryItem(
                                label = displayLabel,
                                percentage = percentage.toInt(),
                                color = moodColors[moodKey] ?: MaterialTheme.colorScheme.outline,
                                isDarkMode = isDarkMode
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarDayItem(
    day: Int,
    mood: MoodType?,
    isSelected: Boolean,
    isDarkMode: Boolean,
    onClick: () -> Unit
) {
    val textColor = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onSurface
    val textSecondary = if (isDarkMode) Color.LightGray else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(PrimaryPurple),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day.toString(),
                    fontSize = 14.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = day.toString(),
                    fontSize = 12.sp,
                    color = textColor,
                    fontWeight = FontWeight.Medium
                )
                if (mood != null) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(getMoodColor(mood)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = getEmojiForMood(mood),
                            fontSize = 11.sp
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(18.dp))
                }
            }
        }
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

@Composable
fun SummaryItem(
    label: String,
    percentage: Int,
    color: Color,
    isDarkMode: Boolean
) {
    val textColor = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            fontSize = 14.sp,
            color = textColor
        )
        Text(
            text = "$percentage%",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = textColor
        )
    }
}

fun getEmojiForMood(mood: MoodType): String = mood.emoji

fun getMoodBgColor(mood: MoodType): Color {
    return getMoodColor(mood).copy(alpha = 0.15f)
}

fun formatDateNicely(dateStr: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formatter = SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH)
        val date = parser.parse(dateStr)
        formatter.format(date!!)
    } catch (e: Exception) {
        dateStr
    }
}
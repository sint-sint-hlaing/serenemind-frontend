package com.serenemind.ui.mood

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
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
import com.serenemind.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodHistoryScreen(
    viewModel: MoodViewModel,
    onBack: () -> Unit = {}
) {
    val summary by viewModel.summaryState.collectAsState()
    val history by viewModel.historyState.collectAsState()
    val selectedMood by viewModel.selectedDateMood.collectAsState()
    
    var calendar by remember { mutableStateOf(Calendar.getInstance()) }
    
    val monthYearFormat = remember { SimpleDateFormat("MMMM yyyy", Locale.ENGLISH) }
    val dayFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    LaunchedEffect(calendar) {
        viewModel.fetchMoodHistory(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mood History", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                    var showMenu by remember { mutableStateOf(false) }
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(
                            text = { Text("Refresh Data") },
                            onClick = { 
                                viewModel.refresh()
                                showMenu = false
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
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
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                                Icon(Icons.Default.ChevronLeft, contentDescription = "Prev")
                            }
                            Text(
                                text = monthYearFormat.format(calendar.time), 
                                fontWeight = FontWeight.Bold, 
                                fontSize = 18.sp,
                                color = TextPrimary
                            )
                            IconButton(onClick = { 
                                val newCal = calendar.clone() as Calendar
                                newCal.add(Calendar.MONTH, 1)
                                calendar = newCal
                            }) {
                                Icon(Icons.Default.ChevronRight, contentDescription = "Next")
                            }
                        }

                        // Calendar Grid Header
                        val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            daysOfWeek.forEach { day ->
                                Text(
                                    text = day, 
                                    color = TextSecondary, 
                                    fontSize = 12.sp, 
                                    modifier = Modifier.weight(1f), 
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        
                        // Calculate days for the grid
                        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                        val firstDayOfMonth = (calendar.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, 1) }
                        val firstDayOfWeek = firstDayOfMonth.get(Calendar.DAY_OF_WEEK)
                        val offset = if (firstDayOfWeek == Calendar.SUNDAY) 6 else firstDayOfWeek - 2
                        val totalCells = daysInMonth + offset
                        
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(7),
                            modifier = Modifier.height(300.dp),
                            userScrollEnabled = false
                        ) {
                            items(totalCells) { index ->
                                if (index >= offset) {
                                    val day = index - offset + 1
                                    val cellDate = (calendar.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, day) }
                                    val dateString = dayFormat.format(cellDate.time)
                                    val moodData = history.find { it.date == dateString }
                                    
                                    val isSelected = selectedMood?.date == dateString
                                    
                                    CalendarDayItem(
                                        day = day,
                                        mood = moodData?.mood,
                                        isSelected = isSelected,
                                        onClick = { viewModel.selectDateMood(dateString) }
                                    )
                                } else {
                                    Box(modifier = Modifier.aspectRatio(1f))
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
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = formatDateNicely(mood.date),
                                color = TextSecondary, 
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
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "${mood.intensity}%", 
                                        color = Success,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    mood.note?.let {
                                        Text(it, color = TextSecondary, fontSize = 14.sp)
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
                        Text("No mood recorded for this day", color = TextSecondary, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Mood Summary
                Text("Mood Summary (This Week)", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
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
                    modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(130.dp), contentAlignment = Alignment.Center) {
                        MoodSummaryPieChart(summary = summary)
                    }
                    
                    Column(modifier = Modifier.padding(start = 32.dp).weight(1f)) {
                        summary.forEach { (mood, percentage) ->
                            val moodKey = mood.uppercase()
                            val displayLabel = mood.lowercase().replaceFirstChar { it.uppercase() }
                            SummaryItem(displayLabel, percentage.toInt(), moodColors[moodKey] ?: Color.Gray)
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
    onClick: () -> Unit
) {
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
                    color = TextPrimary,
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
fun SummaryItem(label: String, percentage: Int, color: Color) {
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
        Text(text = label, modifier = Modifier.weight(1f), fontSize = 14.sp, color = TextPrimary)
        Text(text = "$percentage%", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
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

fun getMoodBgColor(mood: MoodType): Color {
    val color = when (mood) {
        MoodType.HAPPY -> MoodHappy
        MoodType.CALM -> MoodCalm
        MoodType.NEUTRAL -> MoodNeutral
        MoodType.SAD -> MoodSad
        MoodType.ANXIOUS -> MoodAnxious
        MoodType.ANGRY -> MoodAngry
    }
    return color.copy(alpha = 0.15f)
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

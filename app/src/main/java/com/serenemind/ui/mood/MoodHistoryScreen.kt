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
                    IconButton(onClick = { /* More options */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color(0xFFFBFBFF)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
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
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Prev", modifier = Modifier.size(20.dp))
                    }
                    Text(
                        text = monthYearFormat.format(calendar.time), 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 16.sp
                    )
                    IconButton(onClick = { 
                        val newCal = calendar.clone() as Calendar
                        newCal.add(Calendar.MONTH, 1)
                        calendar = newCal
                    }) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Next", modifier = Modifier.size(20.dp))
                    }
                }

                // Calendar Grid Header
                val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    daysOfWeek.forEach { day ->
                        Text(
                            text = day, 
                            color = Color.Gray, 
                            fontSize = 11.sp, 
                            modifier = Modifier.weight(1f), 
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                // Calculate days for the grid
                val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                val firstDayOfWeek = (calendar.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, 1) }.get(Calendar.DAY_OF_WEEK)
                val offset = (firstDayOfWeek + 5) % 7
                val totalCells = daysInMonth + offset
                
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier.height(240.dp), // Reduced height
                    userScrollEnabled = false
                ) {
                    items(totalCells) { index ->
                        if (index >= offset) {
                            val day = index - offset + 1
                            val cellDate = (calendar.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, day) }
                            val dateString = dayFormat.format(cellDate.time)
                            val moodData = history.find { it.date == dateString }
                            
                            CalendarDayItem(
                                day = day, 
                                moodEmoji = moodData?.mood?.let { getEmojiForMood(it) },
                                isSelected = selectedMood?.date == dateString,
                                onClick = { viewModel.selectDateMood(dateString) }
                            )
                        } else {
                            Box(modifier = Modifier.aspectRatio(1f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Selected Day Card
                selectedMood?.let { mood ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = formatDateNicely(mood.date),
                                color = Color.Gray, 
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(getEmojiForMood(mood.mood), fontSize = 42.sp)
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = mood.mood.lowercase().replaceFirstChar { it.uppercase() },
                                        fontWeight = FontWeight.Bold, 
                                        fontSize = 17.sp
                                    )
                                    Text(
                                        text = "${mood.intensity}%", 
                                        color = Color(0xFF4CAF50),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    mood.note?.let {
                                        Text(it, color = Color.Gray, fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }
                } ?: run {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No mood recorded for this day", color = Color.Gray, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Mood Summary
                Text("Mood Summary (This Week)", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Spacer(modifier = Modifier.height(16.dp))
                
                val moodColors = mapOf(
                    "Happy" to Color(0xFFFF9800),
                    "Calm" to Color(0xFF03A9F4),
                    "Neutral" to Color(0xFFFFEB3B),
                    "Sad" to Color(0xFF9C27B0),
                    "Anxious" to Color(0xFF80DEEA),
                    "Angry" to Color(0xFFF44336)
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(130.dp), contentAlignment = Alignment.Center) {
                        MoodSummaryPieChart(summary = summary)
                    }
                    
                    Column(modifier = Modifier.padding(start = 24.dp).weight(1f)) {
                        summary.forEach { (mood, percentage) ->
                            SummaryItem(mood, percentage.toInt(), moodColors[mood] ?: Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

fun getEmojiForMood(mood: String): String {
    return when (mood.lowercase()) {
        "happy" -> "😊"
        "sad" -> "☹️"
        "anxious" -> "😰"
        "angry" -> "😡"
        "calm" -> "😌"
        "neutral" -> "😐"
        else -> "😊"
    }
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

@Composable
fun CalendarDayItem(
    day: Int, 
    moodEmoji: String?, 
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .clip(CircleShape)
            .background(if (isSelected) Color(0xFF673AB7) else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = day.toString(),
                fontSize = 13.sp,
                color = if (isSelected) Color.White else Color.Black,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
            if (moodEmoji != null) {
                Text(text = moodEmoji, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun SummaryItem(label: String, percentage: Int, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, modifier = Modifier.weight(1f), fontSize = 13.sp)
        Text(text = "$percentage%", fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

// GoalDetailScreen.kt
package com.serenemind.ui.goal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.serenemind.model.entity.enums.GoalStatus
import com.serenemind.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailScreen(
    viewModel: GoalViewModel,
    isDarkMode: Boolean,
    onBack: () -> Unit = {},
    onUpdateProgress: () -> Unit = {}
) {
    val goal by viewModel.selectedGoal.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val notes by viewModel.notes.collectAsState()

    val backgroundColor = if (isDarkMode) Color(0xFF1A1A1A) else Color.White
    val surfaceColor = if (isDarkMode) Color(0xFF2A2A2A) else Color(0xFFF9F9F9)
    val borderColor = if (isDarkMode) Color(0xFF333333) else Color(0xFFEEEEEE)
    val textPrimary = if (isDarkMode) Color.White else TextPrimary
    val textSecondary = if (isDarkMode) Color.LightGray else TextSecondary

    // Fetch latest data when screen opens
    LaunchedEffect(goal?.id) {
        goal?.id?.let { id ->
            viewModel.fetchGoalById(id)
            viewModel.fetchNotes(id)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Goal Detail",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = textPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = textPrimary
                        )
                    }
                },
                actions = {},
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = backgroundColor
                )
            )
        },
        containerColor = backgroundColor
    ) { padding ->
        goal?.let { g ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Progress Circle
                val themeColor = remember(g.color, g.icon, g.title) {
                    parseColor(g.color) ?: getGoalIconColor(g.icon ?: g.title, null)
                }

                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(160.dp)) {
                    CircularProgressIndicator(
                        progress = {
                            val target = g.targetDays.toFloat().coerceAtLeast(1f)
                            (g.progress.toFloat() / target).coerceIn(0f, 1f)
                        },
                        modifier = Modifier.fillMaxSize(),
                        color = themeColor,
                        strokeWidth = 10.dp,
                        trackColor = if (isDarkMode) Color(0xFF333333) else Color(0xFFF5F5F5),
                        strokeCap = StrokeCap.Round,
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (!g.icon.isNullOrBlank()) {
                            // Check if it's an emoji or a category name
                            if (g.icon.length <= 2) {
                                Text(g.icon, fontSize = 48.sp)
                            } else {
                                Icon(
                                    getGoalIcon(g.icon, g.title),
                                    contentDescription = null,
                                    tint = themeColor,
                                    modifier = Modifier.size(56.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Status badge
                Surface(
                    color = themeColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(100.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(themeColor)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = g.status.name.lowercase().replaceFirstChar { it.uppercase() },
                            color = themeColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    g.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary,
                    textAlign = TextAlign.Center
                )
                
                if (!g.description.isNullOrBlank()) {
                    Text(
                        g.description,
                        fontSize = 14.sp,
                        color = textSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Progress Info Row
                Text(
                    "Progress",
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = textPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                InfoCard(
                    label = "",
                    value = "${g.progress} / ${g.targetDays} days",
                    progress = g.progress.toFloat() / g.targetDays.toFloat().coerceAtLeast(1f),
                    color = themeColor,
                    isDarkMode = isDarkMode,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                // History
                Text(
                    "History",
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = textPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))

                val historyList = g.history ?: emptyList()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val today = LocalDate.now()
                    val days = (0..6).map { today.minusDays(it.toLong()) }.reversed()
                    
                    days.forEach { date ->
                        val dayName = date.dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
                        val dateLabel = date.dayOfMonth.toString()
                        
                        val historyEntry = historyList.find { 
                            try { 
                                val entryDateStr = if (it.date.contains("T")) it.date.substringBefore("T") else it.date
                                LocalDate.parse(entryDateStr) == date 
                            } catch(e: Exception) { false }
                        }
                        
                        HistoryItem(
                            day = dayName,
                            dateLabel = dateLabel,
                            completedStatus = historyEntry?.completed,
                            isToday = date == today,
                            isDarkMode = isDarkMode
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Notes Section
                Text(
                    "Notes",
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = textPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (notes.isNotEmpty()) {
                    notes.forEach { note ->
                        var isEditing by remember { mutableStateOf(false) }
                        var editContent by remember { mutableStateOf(note.content ?: "") }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = surfaceColor
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                if (isEditing) {
                                    OutlinedTextField(
                                        value = editContent,
                                        onValueChange = { editContent = it },
                                        modifier = Modifier.fillMaxWidth(),
                                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp)
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        TextButton(onClick = { isEditing = false }) {
                                            Text("Cancel")
                                        }
                                        TextButton(onClick = {
                                            note.id?.let {
                                                viewModel.updateNote(g.id, it, editContent)
                                            }
                                            isEditing = false
                                        }) {
                                            Text("Save")
                                        }
                                    }
                                } else {
                                    Text(
                                        text = note.content ?: "",
                                        fontSize = 14.sp,
                                        color = textPrimary
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = formatDateNicely(note.createdAt ?: ""),
                                            fontSize = 11.sp,
                                            color = textSecondary,
                                            modifier = Modifier.weight(1f)
                                        )
                                        IconButton(onClick = {
                                            note.id?.let {
                                                viewModel.deleteNote(g.id, it)
                                            }
                                        }, modifier = Modifier.size(24.dp)) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Text(
                        "No notes yet. Add one below!",
                        color = textSecondary,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Add note input
                var noteInput by remember { mutableStateOf("") }
                var isSavingNote by remember { mutableStateOf(false) }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = noteInput,
                        onValueChange = { noteInput = it },
                        placeholder = {
                            Text(
                                "Add a note...",
                                fontSize = 14.sp,
                                color = textSecondary
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = borderColor,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedContainerColor = surfaceColor,
                            focusedContainerColor = surfaceColor
                        )
                    )

                    Button(
                        onClick = {
                            if (noteInput.isNotBlank()) {
                                isSavingNote = true
                                viewModel.addNote(g.id, noteInput)
                                noteInput = ""
                                isSavingNote = false
                            }
                        },
                        enabled = noteInput.isNotBlank() && !isSavingNote,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.height(60.dp)
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Save Note",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Main Action Button
                if (g.status == GoalStatus.ACTIVE || g.status == GoalStatus.PAUSED) {
                    Button(
                        onClick = onUpdateProgress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text(
                            if (g.status == GoalStatus.PAUSED) "Resume & Update" else "Update Progress",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        } ?: run {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (uiState is GoalUiState.Loading) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Loading goal details...", color = textSecondary)
                    } else {
                        Text("📭", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Goal not found", color = textSecondary, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onBack) {
                            Text("Go Back")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoCard(
    label: String,
    value: String,
    progress: Float,
    color: Color,
    isDarkMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    val textPrimary = if (isDarkMode) Color.White else TextPrimary
    val textSecondary = if (isDarkMode) Color.LightGray else TextSecondary
    val trackColor = if (isDarkMode) Color(0xFF333333) else Color(0xFFF5F5F5)

    Column(modifier = modifier) {
        if (label.isNotEmpty()) {
            Text(label, fontSize = 13.sp, color = textSecondary, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
        }
        Text(value, fontSize = 15.sp, color = textPrimary, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = color,
            trackColor = trackColor
        )
    }
}

@Composable
fun HistoryItem(
    day: String,
    dateLabel: String,
    completedStatus: Boolean?,
    isToday: Boolean,
    isDarkMode: Boolean = false
) {
    val textSecondary = if (isDarkMode) Color.LightGray else TextSecondary
    val borderColor = if (isDarkMode) Color(0xFF333333) else Color(0xFFEEEEEE)
    val surfaceColor = if (isDarkMode) Color(0xFF2A2A2A) else Color(0xFFF5F5F5)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = day,
            fontSize = 12.sp,
            color = textSecondary,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(
                    when (completedStatus) {
                        true -> Success.copy(alpha = 0.15f)
                        false -> Color.Red.copy(alpha = 0.1f)
                        null -> if (isToday) surfaceColor else Color.Transparent
                    }
                )
                .then(
                    if (completedStatus == null && !isToday)
                        Modifier.border(1.dp, borderColor, CircleShape)
                    else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            if (completedStatus == true) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = Success,
                    modifier = Modifier.size(20.dp)
                )
            } else if (completedStatus == false) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.Red,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            dateLabel,
            fontSize = 11.sp,
            color = if (isToday) PrimaryPurple else textSecondary,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
        )
    }
}

fun parseColor(colorStr: String?): Color? {
    return when (colorStr?.lowercase()) {
        "red" -> Color(0xFFE53935)
        "blue" -> Color(0xFF1E88E5)
        "green" -> Color(0xFF43A047)
        "yellow" -> Color(0xFFFBC02D)
        "purple" -> Color(0xFF8E24AA)
        "orange" -> Color(0xFFFB8C00)
        "pink" -> Color(0xFFD81B60)
        "teal" -> Color(0xFF00897B)
        else -> null
    }
}

fun formatDateNicely(dateStr: String): String {
    return try {
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val date = java.time.LocalDateTime.parse(dateStr, formatter)
        date.format(DateTimeFormatter.ofPattern("MMM dd, HH:mm", Locale.ENGLISH))
    } catch (e: Exception) {
        dateStr.substringBefore("T")
    }
}

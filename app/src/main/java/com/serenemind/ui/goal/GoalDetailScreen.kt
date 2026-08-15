// GoalDetailScreen.kt (Complete fixed version)
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MoreVert
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
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailScreen(
    viewModel: GoalViewModel,
    isDarkMode: Boolean,
    onBack: () -> Unit = {}
) {
    val goal by viewModel.selectedGoal.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val notes by viewModel.notes.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    var noteText by remember { mutableStateOf("") }
    var showNoteSaved by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    val backgroundColor = if (isDarkMode) Color(0xFF1A1A1A) else Color.White
    val surfaceColor = if (isDarkMode) Color(0xFF2A2A2A) else Color(0xFFF9F9F9)
    val borderColor = if (isDarkMode) Color(0xFF333333) else Color(0xFFEEEEEE)
    val textPrimary = if (isDarkMode) Color.White else TextPrimary
    val textSecondary = if (isDarkMode) Color.LightGray else TextSecondary

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
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "More",
                            tint = textPrimary
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        containerColor = if (isDarkMode) Color(0xFF2A2A2A) else Color.White
                    ) {
                        goal?.let { g ->
                            when (g.status) {
                                GoalStatus.ACTIVE -> {
                                    DropdownMenuItem(
                                        text = { Text("⏸️ Pause Goal", color = textPrimary) },
                                        onClick = {
                                            viewModel.pauseGoal(g.id)
                                            showMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("✅ Complete Goal", color = Success) },
                                        onClick = {
                                            viewModel.completeGoal(g.id)
                                            showMenu = false
                                        }
                                    )
                                }
                                GoalStatus.PAUSED -> {
                                    DropdownMenuItem(
                                        text = { Text("▶️ Resume Goal", color = PrimaryLight) },
                                        onClick = {
                                            viewModel.resumeGoal(g.id)
                                            showMenu = false
                                        }
                                    )
                                }
                                GoalStatus.COMPLETED -> {
                                    DropdownMenuItem(
                                        text = { Text("✅ Already Completed", color = Success) },
                                        enabled = false,
                                        onClick = {}
                                    )
                                }
                                else -> {}
                            }
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "🗑️ Delete Goal",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                },
                                onClick = {
                                    viewModel.deleteGoal(g.id)
                                    showMenu = false
                                    onBack()
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = backgroundColor
                )
            )
        },
        containerColor = backgroundColor
    ) { padding ->
        when {
            uiState is GoalUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Loading...", color = textSecondary)
                    }
                }
            }
            uiState is GoalUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("❌", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            (uiState as GoalUiState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.fetchGoals() }) {
                            Text("Retry")
                        }
                    }
                }
            }
            goal != null -> {
                val g = goal!!

                LaunchedEffect(g.id) {
                    viewModel.fetchGoalById(g.id)
                    viewModel.fetchNotes(g.id)
                }

                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Progress Circle
                    val themeColor = remember(g.color) {
                        parseColor(g.color) ?: when {
                            g.status == GoalStatus.COMPLETED -> Success
                            g.status == GoalStatus.PAUSED -> Warning
                            g.status == GoalStatus.EXPIRED -> Color.Red
                            else -> PrimaryLight
                        }
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
                            if (g.icon != null) {
                                Text(g.icon, fontSize = 32.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                            Text(
                                g.progress.toString(),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                            HorizontalDivider(
                                modifier = Modifier.width(30.dp).padding(vertical = 4.dp),
                                thickness = 2.dp,
                                color = borderColor
                            )
                            Text(
                                g.targetDays.toString(),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = textSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Status badge
                    when (g.status) {
                        GoalStatus.COMPLETED -> {
                            Badge(containerColor = Success, modifier = Modifier.padding(4.dp)) {
                                Text("✅ Completed", color = Color.White, fontSize = 12.sp)
                            }
                        }
                        GoalStatus.PAUSED -> {
                            Badge(containerColor = Warning, modifier = Modifier.padding(4.dp)) {
                                Text("⏸️ Paused", color = Color.White, fontSize = 12.sp)
                            }
                        }
                        GoalStatus.EXPIRED -> {
                            Badge(containerColor = Color.Red, modifier = Modifier.padding(4.dp)) {
                                Text("⏰ Expired", color = Color.White, fontSize = 12.sp)
                            }
                        }
                        GoalStatus.ACTIVE -> {
                            Badge(containerColor = themeColor, modifier = Modifier.padding(4.dp)) {
                                Text("🟢 Active", color = Color.White, fontSize = 12.sp)
                            }
                        }
                        else -> {}
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        g.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        g.description ?: "Build a calm and peaceful mind.",
                        fontSize = 14.sp,
                        color = textSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Progress Info Row
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        InfoCard(
                            label = "Progress",
                            value = "${g.progress} / ${g.targetDays} days",
                            progress = g.progress.toFloat() / g.targetDays.toFloat().coerceAtLeast(1f),
                            color = themeColor,
                            isDarkMode = isDarkMode,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        InfoCard(
                            label = "Streak",
                            value = "${g.streak} days",
                            progress = (g.streak.toFloat() / 30f).coerceAtMost(1f),
                            color = Warning,
                            isDarkMode = isDarkMode,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Streak Fire Icons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        repeat(7) { i ->
                            Text(
                                "🔥",
                                fontSize = 18.sp,
                                modifier = Modifier.alpha(if (i < g.streak) 1f else 0.2f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // History
                    Text(
                        "History",
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = textPrimary
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val historyList = g.history ?: emptyList()
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        val days = listOf("May 6", "May 7", "May 8", "May 9", "May 10", "May 11", "May 12")
                        days.forEachIndexed { index, day ->
                            val historyEntry = historyList.find { it.date.contains(day) }
                            val isCompleted = historyEntry?.completed ?: (index < 3 || index == 5)
                            val isToday = index == 6
                            HistoryItem(day, isCompleted, isToday, isDarkMode)
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

                    // Display notes from ViewModel
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
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            IconButton(onClick = { isEditing = true }, modifier = Modifier.size(24.dp)) {
                                                Icon(Icons.Default.MoreHoriz, contentDescription = "Edit", tint = textSecondary, modifier = Modifier.size(16.dp))
                                            }
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
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Add note input
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = noteText,
                            onValueChange = { noteText = it },
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
                                if (noteText.isNotBlank()) {
                                    viewModel.addNote(g.id, noteText)
                                    android.widget.Toast.makeText(
                                        context,
                                        "Note saved",
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                    noteText = ""
                                    showNoteSaved = true
                                }
                            },
                            enabled = noteText.isNotBlank(),
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

                    if (showNoteSaved) {
                        Text(
                            "✅ Note saved!",
                            color = Success,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        LaunchedEffect(Unit) {
                            delay(2000)
                            showNoteSaved = false
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action Buttons based on status
                    when (g.status) {
                        GoalStatus.ACTIVE -> {
                            Button(
                                onClick = { viewModel.incrementProgress(g.id) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                enabled = uiState !is GoalUiState.Loading
                            ) {
                                if (uiState is GoalUiState.Loading) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Updating...", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                } else {
                                    Text("✅ Check In", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                            }
                        }
                        GoalStatus.PAUSED -> {
                            Button(
                                onClick = { viewModel.resumeGoal(g.id) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Warning)
                            ) {
                                Text("▶️ Resume Goal", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                        GoalStatus.COMPLETED -> {
                            Button(
                                onClick = {},
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                enabled = false,
                                colors = ButtonDefaults.buttonColors(containerColor = Success)
                            ) {
                                Text("✅ Goal Completed", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                        GoalStatus.EXPIRED -> {
                            Button(
                                onClick = {},
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                enabled = false,
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                            ) {
                                Text("⏰ Goal Expired", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                        else -> {}
                    }
                }
            }
            else -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📭", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No goal selected", color = textSecondary, fontSize = 16.sp)
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
        Text(label, fontSize = 13.sp, color = textSecondary, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(4.dp))
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
    isCompleted: Boolean,
    isToday: Boolean,
    isDarkMode: Boolean = false
) {
    val textSecondary = if (isDarkMode) Color.LightGray else TextSecondary
    val borderColor = if (isDarkMode) Color(0xFF333333) else Color(0xFFEEEEEE)
    val surfaceColor = if (isDarkMode) Color(0xFF2A2A2A) else Color(0xFFF5F5F5)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isCompleted -> Success.copy(alpha = 0.15f)
                        isToday -> surfaceColor
                        else -> Color.Transparent
                    }
                )
                .then(
                    if (!isCompleted && !isToday)
                        Modifier.border(1.dp, borderColor, CircleShape)
                    else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            when {
                isCompleted -> {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = Success,
                        modifier = Modifier.size(16.dp)
                    )
                }
                isToday -> {
                    Icon(
                        Icons.Default.MoreHoriz,
                        contentDescription = null,
                        tint = textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            day,
            fontSize = 10.sp,
            color = if (isToday) PrimaryLight else textSecondary,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
        )
    }
}

fun parseColor(colorStr: String?): Color? {
    return when (colorStr?.lowercase()) {
        "red" -> Color(0xFFE53935)
        "blue" -> Color(0xFF1E88E5)
        "green" -> Color(0xFF43A047)
        "yellow" -> Color(0xFFFFEB3B)
        "purple" -> Color(0xFF8E24AA)
        "orange" -> Color(0xFFFB8C00)
        "pink" -> Color(0xFFD81B60)
        "teal" -> Color(0xFF00897B)
        else -> null
    }
}

package com.serenemind.ui.streak

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.serenemind.model.response.StreakResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreakScreen(
    viewModel: StreakViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showMissedDay by remember { mutableStateOf(false) }
    var showNewBest by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchStreak(isSilent = true)
    }

    LaunchedEffect(uiState) {
        if (uiState is StreakUiState.Success) {
            val streak = (uiState as StreakUiState.Success).streak
            if (streak.isNewBest) {
                showNewBest = true
            }
        }
    }

    if (showNewBest) {
        NewBestCelebrationScreen(
            streak = (uiState as? StreakUiState.Success)?.streak?.currentStreak ?: 0,
            onDismiss = { showNewBest = false }
        )
    } else if (showMissedDay) {
        StreakProtectionScreen(
            onUseFreeze = {
                viewModel.useFreeze()
                showMissedDay = false
            },
            onDismiss = { showMissedDay = false }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Streak", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        val streak = (uiState as? StreakUiState.Success)?.streak
                        if (streak != null && streak.streakFreezeCount > 0) {
                            IconButton(onClick = { showMissedDay = true }) {
                                Icon(Icons.Default.Shield, contentDescription = "Streak Protection", tint = Color(0xFF673AB7))
                            }
                        }
                        IconButton(onClick = { /* Info */ }) {
                            Icon(Icons.Default.Info, contentDescription = "Info")
                        }
                    }
                )
            }
        ) { padding ->
            when (val state = uiState) {
                is StreakUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is StreakUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = Color.Red)
                    }
                }
                is StreakUiState.Success -> {
                    StreakContent(
                        streak = state.streak,
                        modifier = Modifier.padding(padding)
                    )
                }
            }
        }
    }
}

@Composable
fun StreakContent(
    streak: StreakResponse,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Main Streak Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "🔥", fontSize = 48.sp)
                Text(
                    text = streak.currentStreak.toString(),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Day Streak",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Amazing! 🔥",
                    color = Color(0xFFF44336),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Weekly Overview
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            days.forEachIndexed { index, day ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = day, fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    val isChecked = streak.weeklyOverview.getOrElse(index) { false }
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (isChecked) Color(0xFF4CAF50) else Color(0xFFF5F5F5)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isChecked) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Statistics
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                StreakStatItem("Current Streak", "${streak.currentStreak} days", Icons.Default.Timer)
                Divider(modifier = Modifier.padding(vertical = 12.dp))
                StreakStatItem("Longest Streak", "${streak.longestStreak} days", Icons.Default.Timeline)
                Divider(modifier = Modifier.padding(vertical = 12.dp))
                StreakStatItem("Total Completed", "${streak.totalCompletedDays} days", Icons.Default.CheckCircle)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8EAF6))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Keep it up!", fontWeight = FontWeight.Bold)
                    Text(text = "Consistency is the key to a better you.", fontSize = 12.sp)
                }
                Text(text = "🚩", fontSize = 32.sp)
            }
        }
    }
}

@Composable
fun StreakStatItem(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color(0xFF673AB7))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = label, fontWeight = FontWeight.Medium)
        }
        Text(text = value, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun StreakProtectionScreen(
    onUseFreeze: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = onDismiss,
            modifier = Modifier.align(Alignment.Start)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Missed a Day?", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))
        Text(text = "☁️", fontSize = 80.sp) // Cloud with rain icon
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "It's okay, we all have\noff days.",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Don't break your steak!\nUse a Freeze or get right\nback on track tomorrow.",
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = onUseFreeze,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7))
        ) {
            Text("Use Freeze")
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Maybe Later")
        }
    }
}

@Composable
fun NewBestCelebrationScreen(
    streak: Int,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = onDismiss,
            modifier = Modifier.align(Alignment.Start)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(text = "🔥", fontSize = 80.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Surface(
            color = Color(0xFFFFC107),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "New Best!",
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "$streak Days Steak! 🔥",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "You've achieved your\nlongest steak.",
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "You're on fire! Keep it up!",
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = { /* Share */ },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
        ) {
            Text("Share Achievement")
        }
        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = onDismiss) {
            Text("View My Progress", color = Color.White)
        }
    }
}

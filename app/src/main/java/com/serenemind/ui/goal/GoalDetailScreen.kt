package com.serenemind.ui.goal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.serenemind.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailScreen(
    viewModel: GoalViewModel,
    onBack: () -> Unit = {}
) {
    val goal by viewModel.selectedGoal.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Goal Detail", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
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
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        goal?.let { g ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                
                // Circular Progress
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(160.dp)) {
                    CircularProgressIndicator(
                        progress = { g.progress.toFloat() / g.targetDays.toFloat() },
                        modifier = Modifier.fillMaxSize(),
                        color = Success,
                        strokeWidth = 12.dp,
                        trackColor = Color(0xFFF0F0F0),
                        strokeCap = StrokeCap.Round
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = g.progress.toString(), fontSize = 48.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                        Text(text = "/${g.targetDays}", fontSize = 18.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
                
                Text(g.title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = g.description ?: "Build a calm and peaceful mind.", 
                    color = TextSecondary, 
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Progress Card
                DetailCard(title = "Progress", value = "${g.progress} / ${g.targetDays} days") {
                    LinearProgressIndicator(
                        progress = { g.progress.toFloat() / g.targetDays.toFloat() },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                        color = Success,
                        trackColor = Color(0xFFF0F0F0)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Streak Card
                DetailCard(title = "Streak", value = "7 days") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val streak = 7
                        repeat(10) { index ->
                            Icon(
                                imageVector = Icons.Default.Whatshot,
                                contentDescription = null,
                                tint = if (index < streak) Color(0xFFFF7043) else Color(0xFFE0E0E0),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // History Card
                DetailCard(title = "History", value = "") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        HistoryNode("May 6", true)
                        HistoryNode("May 7", true)
                        HistoryNode("May 8", true)
                        HistoryNode("May 9", true)
                        HistoryNode("May 10", true)
                        HistoryNode("May 11", false)
                        HistoryNode("May 12", null)
                    }
                }
                
                Spacer(modifier = Modifier.height(40.dp))
                
                Button(
                    onClick = { viewModel.incrementProgress(g.id) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Complete Today", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun DetailCard(title: String, value: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                if (value.isNotEmpty()) {
                    Text(value, color = if (title == "Streak") MaterialTheme.colorScheme.primary else TextSecondary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
fun HistoryNode(date: String, completed: Boolean?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    when (completed) {
                        true -> Color(0xFFE8F5E9)
                        false -> Color(0xFFFFEBEE)
                        else -> Color(0xFFF5F5F5)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            when (completed) {
                true -> Icon(Icons.Default.Check, null, tint = Success, modifier = Modifier.size(18.dp))
                false -> Text("✕", color = Error, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                else -> Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFE0E0E0)))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = date,
            fontSize = 10.sp,
            color = TextSecondary,
            fontWeight = FontWeight.Bold
        )
    }
}

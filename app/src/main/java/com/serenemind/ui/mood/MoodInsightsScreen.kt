package com.serenemind.ui.mood

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.serenemind.model.entity.enums.MoodType
import com.serenemind.ui.theme.TextPrimary
import com.serenemind.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodInsightsScreen(
    viewModel: MoodViewModel,
    onBack: () -> Unit
) {
    val summary by viewModel.summaryState.collectAsState()
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mood Insights", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // Filter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("This Month", fontSize = 14.sp, color = TextSecondary)
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = TextSecondary)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Dominant Mood Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFFFF9C4)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("😊", fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Most Frequent Mood", fontSize = 12.sp, color = TextSecondary)
                        Text("Happy", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("45% of days", fontSize = 13.sp, color = Color(0xFF4CAF50))
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Mood Trend Chart
            Text("Mood Trend", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
            Spacer(modifier = Modifier.height(16.dp))
            
            MoodTrendChart()

            Spacer(modifier = Modifier.height(32.dp))

            // Mood Distribution
            Text("Mood Distribution", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                MoodType.values().forEach { type ->
                    val percentage = summary[type.name] ?: 0.0
                    MoodDistributionItem(type, percentage.toInt())
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun MoodTrendChart() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color.White)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val points = listOf(0.7f, 0.5f, 0.8f, 0.4f, 0.6f, 0.9f, 0.7f)
            val stepX = width / (points.size - 1)
            
            val path = Path().apply {
                moveTo(0f, height * (1 - points[0]))
                points.forEachIndexed { index, point ->
                    if (index > 0) {
                        lineTo(index * stepX, height * (1 - point))
                    }
                }
            }
            
            drawPath(
                path = path,
                color = Color(0xFF6750A4),
                style = Stroke(width = 3.dp.toPx())
            )
            
            // Draw points
            points.forEachIndexed { index, point ->
                drawCircle(
                    color = Color(0xFF6750A4),
                    radius = 4.dp.toPx(),
                    center = Offset(index * stepX, height * (1 - point))
                )
            }
        }
    }
}

@Composable
fun MoodDistributionItem(type: MoodType, percentage: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(type.emoji, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .width(32.dp)
                .height(80.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(percentage / 100f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(getMoodColor(type))
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("$percentage%", fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

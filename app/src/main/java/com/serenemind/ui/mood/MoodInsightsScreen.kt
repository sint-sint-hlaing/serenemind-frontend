// MoodInsightsScreen.kt
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.serenemind.model.entity.enums.MoodType
import com.serenemind.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodInsightsScreen(
    viewModel: MoodViewModel,
    isDarkMode: Boolean,
    onBack: () -> Unit
) {
    val summary by viewModel.summaryState.collectAsState()

    // Get colors based on dark mode
    val backgroundColor = if (isDarkMode) Color(0xFF1A1A1A) else MaterialTheme.colorScheme.background
    val surfaceColor = if (isDarkMode) Color(0xFF2A2A2A) else Color(0xFFF9F9F9)
    val textColor = if (isDarkMode) Color.White else TextPrimary
    val textSecondary = if (isDarkMode) Color.LightGray else TextSecondary
    val cardColor = if (isDarkMode) Color(0xFF2A2A2A) else Color(0xFFF9F9F9)
    val chartBackground = if (isDarkMode) Color(0xFF1A1A1A) else Color.White

    // Calculate dominant mood
    val dominantMood = summary.maxByOrNull { it.value }?.key?.let {
        MoodType.valueOf(it)
    } ?: MoodType.NEUTRAL
    val dominantPercentage = summary.maxByOrNull { it.value }?.value ?: 0.0

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Mood Insights",
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
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = if (isDarkMode) Color(0xFF1A1A1A) else MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = backgroundColor
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
                Text(
                    "This Month",
                    fontSize = 14.sp,
                    color = textSecondary
                )
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = textSecondary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Dominant Mood Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = cardColor
                )
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                if (isDarkMode)
                                    getMoodColor(dominantMood).copy(alpha = 0.2f)
                                else
                                    getMoodColor(dominantMood).copy(alpha = 0.15f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            dominantMood.emoji,
                            fontSize = 24.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            "Most Frequent Mood",
                            fontSize = 12.sp,
                            color = textSecondary
                        )
                        Text(
                            dominantMood.name.lowercase().replaceFirstChar { it.uppercase() },
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                        Text(
                            "${dominantPercentage.toInt()}% of days",
                            fontSize = 13.sp,
                            color = Success
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Mood Trend Chart
            Text(
                "Mood Trend",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = textColor
            )
            Spacer(modifier = Modifier.height(16.dp))

            MoodTrendChart(
                summary = summary,
                isDarkMode = isDarkMode
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Mood Distribution
            Text(
                "Mood Distribution",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = textColor
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MoodType.values().forEach { type ->
                    val percentage = summary[type.name] ?: 0.0
                    MoodDistributionItem(
                        type = type,
                        percentage = percentage.toInt(),
                        isDarkMode = isDarkMode
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun MoodTrendChart(
    summary: Map<String, Double>,
    isDarkMode: Boolean
) {
    val chartColor = if (isDarkMode) Color(0xFF7C4DFF) else Color(0xFF6750A4)
    val chartBackground = if (isDarkMode) Color(0xFF1A1A1A) else Color.White
    val gridColor = if (isDarkMode) Color(0xFF333333) else Color(0xFFE0E0E0)

    // Generate sample trend data from summary
    val moodValues = listOf(
        summary["HAPPY"] ?: 0.0,
        summary["CALM"] ?: 0.0,
        summary["NEUTRAL"] ?: 0.0,
        summary["SAD"] ?: 0.0,
        summary["ANXIOUS"] ?: 0.0,
        summary["ANGRY"] ?: 0.0
    )

    val maxValue = moodValues.maxOrNull()?.coerceAtLeast(1.0) ?: 1.0
    val normalizedPoints = moodValues.map { it / maxValue }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(chartBackground)
            .clip(RoundedCornerShape(16.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val padding = 40f
            val chartWidth = width - padding * 2
            val chartHeight = height - padding * 2

            // Draw horizontal grid lines
            for (i in 0..4) {
                val y = padding + (chartHeight / 4) * i
                drawLine(
                    color = gridColor,
                    start = Offset(padding, y),
                    end = Offset(width - padding, y),
                    strokeWidth = 1f
                )
            }

            if (normalizedPoints.isNotEmpty()) {
                val points = normalizedPoints
                val stepX = chartWidth / (points.size - 1).coerceAtLeast(1)

                // Draw area fill
                val areaPath = Path().apply {
                    moveTo(padding, height - padding)
                    points.forEachIndexed { index, point ->
                        val x = padding + index * stepX
                        val y = padding + chartHeight * (1 - point.toFloat())
                        lineTo(x, y)
                    }
                    lineTo(padding + (points.size - 1) * stepX, height - padding)
                    close()
                }

                drawPath(
                    path = areaPath,
                    color = chartColor.copy(alpha = 0.2f),
                    style = Stroke(width = 0f)
                )

                // Draw line
                val path = Path().apply {
                    points.forEachIndexed { index, point ->
                        val x = padding + index * stepX
                        val y = padding + chartHeight * (1 - point.toFloat())
                        if (index == 0) {
                            moveTo(x, y)
                        } else {
                            lineTo(x, y)
                        }
                    }
                }

                drawPath(
                    path = path,
                    color = chartColor,
                    style = Stroke(width = 3.dp.toPx())
                )

                // Draw points
                points.forEachIndexed { index, point ->
                    val x = padding + index * stepX
                    val y = padding + chartHeight * (1 - point.toFloat())
                    drawCircle(
                        color = chartColor,
                        radius = 5.dp.toPx(),
                        center = Offset(x, y)
                    )
                }
            }
        }
    }
}

@Composable
fun MoodDistributionItem(
    type: MoodType,
    percentage: Int,
    isDarkMode: Boolean
) {
    val textColor = if (isDarkMode) Color.White else TextPrimary
    val textSecondary = if (isDarkMode) Color.LightGray else TextSecondary
    val trackColor = if (isDarkMode) Color(0xFF333333) else Color(0xFFF5F5F5)
    val color = getMoodColor(type)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
    ) {
        Text(
            type.emoji,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .width(32.dp)
                .height(80.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(trackColor),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(percentage / 100f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (percentage > 0) color else Color.Transparent
                    )
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "$percentage%",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (percentage > 0) textColor else textSecondary
        )
    }
}
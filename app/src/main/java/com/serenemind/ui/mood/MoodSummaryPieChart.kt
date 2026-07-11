package com.serenemind.ui.mood

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun MoodSummaryPieChart(summary: Map<String, Double>) {
    val moodColors = mapOf(
        "Happy" to Color(0xFFFF9800),
        "Calm" to Color(0xFF03A9F4),
        "Neutral" to Color(0xFFFFEB3B),
        "Sad" to Color(0xFF9C27B0),
        "Anxious" to Color(0xFF80DEEA),
        "Angry" to Color(0xFFF44336)
    )
    
    var startAngle = -90f // Start from the top

    Canvas(modifier = Modifier.size(150.dp)) {
        if (summary.isEmpty()) {
            drawArc(
                color = Color.LightGray,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 25.dp.toPx())
            )
        } else {
            summary.forEach { (mood, percentage) ->
                val sweepAngle = (percentage.toFloat() / 100f) * 360f
                
                drawArc(
                    color = moodColors[mood] ?: Color.Gray,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = 25.dp.toPx(), cap = StrokeCap.Round)
                )
                startAngle += sweepAngle
            }
        }
    }
}

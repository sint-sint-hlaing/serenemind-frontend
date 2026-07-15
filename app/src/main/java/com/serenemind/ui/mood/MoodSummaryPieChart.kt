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
        "HAPPY" to Color(0xFFFF9800),
        "CALM" to Color(0xFF03A9F4),
        "NEUTRAL" to Color(0xFFFFEB3B),
        "SAD" to Color(0xFF9C27B0),
        "ANXIOUS" to Color(0xFF80DEEA),
        "ANGRY" to Color(0xFFF44336)
    )
    
    var startAngle = -90f // Start from the top

    Canvas(modifier = Modifier.size(130.dp)) {
        if (summary.isEmpty()) {
            drawArc(
                color = Color.LightGray,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 30f)
            )
        } else {
            val total = summary.values.sum().toFloat()
            summary.forEach { (mood, value) ->
                val sweepAngle = (value.toFloat() / total) * 360f
                
                drawArc(
                    color = moodColors[mood.uppercase()] ?: Color.Gray,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = 30f, cap = StrokeCap.Round)
                )
                startAngle += sweepAngle
            }
        }
    }
}

package com.serenemind.ui.mood

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.serenemind.ui.theme.*

@Composable
fun MoodSummaryPieChart(summary: Map<String, Double>) {
    val outlineColor = MaterialTheme.colorScheme.outlineVariant
    val moodColors = mapOf(
        "HAPPY" to MoodHappy,
        "CALM" to MoodCalm,
        "NEUTRAL" to MoodNeutral,
        "SAD" to MoodSad,
        "ANXIOUS" to MoodAnxious,
        "ANGRY" to MoodAngry
    )
    
    var startAngle = -90f // Start from the top

    Canvas(modifier = Modifier.size(130.dp)) {
        if (summary.isEmpty()) {
            drawArc(
                color = outlineColor,
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

package com.serenemind.ui.mood

import androidx.compose.foundation.Canvas // Ensure this is imported
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MoodSummaryPieChart(summary: Map<String, Double>) {
    val colors = listOf(Color.Blue, Color.Green, Color.Yellow, Color.Red)
    var startAngle = 0f

    Canvas(modifier = Modifier.size(200.dp)) {
        summary.values.forEachIndexed { index, percentage ->
            val sweepAngle = (percentage.toFloat() / 100f) * 360f

            // This now calls the correct Compose DrawScope function
            drawArc(
                color = colors[index % colors.size],
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                size = size // Added size parameter required by Compose
            )
            startAngle += sweepAngle
        }
    }
}
// DELETE THE drawArc FUNCTION THAT WAS HERE
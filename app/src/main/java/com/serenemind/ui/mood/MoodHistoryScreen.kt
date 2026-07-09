package com.serenemind.ui.mood

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MoodHistoryScreen(viewModel: MoodViewModel) {
    // Ensure you have this import for 'by' and collectAsState
    val summary by viewModel.summaryState.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Mood Summary (This Week)", style = MaterialTheme.typography.titleMedium)

        // Display Pie Chart if data is loaded
        if (summary.isNotEmpty()) {
            MoodSummaryPieChart(summary)

            // List the summary values
            summary.forEach { (mood, percentage) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(mood)
                    Text("${percentage.toInt()}%")
                }
            }
        } else {
            Text("No data available for this week.")
        }
    }
}
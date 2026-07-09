package com.serenemind.ui.goal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.* // Ensure you are using Material3
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GoalsScreen(viewModel: GoalViewModel) {
    // FIX 1: Correctly collecting the state
    val goals by viewModel.goals.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchGoals()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(goals) { goal ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = goal.title, style = MaterialTheme.typography.titleMedium)
                    Text(text = "${goal.progress} / ${goal.targetDays} days")

                    Spacer(modifier = Modifier.height(8.dp))

                    // FIX 2: Added a safe calculation to prevent division by zero
                    val progressValue = if (goal.targetDays > 0)
                        goal.progress.toFloat() / goal.targetDays.toFloat() else 0f

                    LinearProgressIndicator(
                        progress = { progressValue },
                        modifier = Modifier.fillMaxWidth().height(8.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // FIX 3: Status check
                    if (goal.status != "COMPLETED") {
                        Button(onClick = { viewModel.incrementProgress(goal.id) }) {
                            Text("Log Progress")
                        }
                    }
                }
            }
        }
    }
}
package com.serenemind.ui.mood

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.serenemind.model.entity.enums.MoodType
import com.serenemind.model.request.MoodRequest

@Composable
fun MoodTrackerScreen(viewModel: MoodViewModel) {
    var intensity by remember { mutableStateOf(40f) }
    var note by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf<MoodType?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("How are you feeling today?", style = MaterialTheme.typography.titleLarge)

        // Mood Selection Grid
        LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.height(200.dp)) {
            items(MoodType.values().size) { index ->
                val mood = MoodType.values()[index]
                Button(onClick = { selectedMood = mood }) {
                    Text(mood.name)
                }
            }
        }

        Slider(value = intensity, onValueChange = { intensity = it }, valueRange = 0f..100f)

        OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("Note") })

        Button(onClick = {
            if (selectedMood != null) {
                viewModel.saveMood(
                    MoodRequest(selectedMood!!, intensity.toInt(), note),
                    intensity = TODO(),
                    note = TODO()
                )
            }
        }) {
            Text("Save")
        }
    }
}
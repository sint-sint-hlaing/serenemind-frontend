package com.serenemind.ui.mood

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.serenemind.model.entity.enums.MoodType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodTrackerScreen(
    viewModel: MoodViewModel,
    onBack: () -> Unit = {},
    onViewHistory: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var intensity by remember { mutableFloatStateOf(40f) }
    var note by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf<MoodType?>(MoodType.NEUTRAL) }

    LaunchedEffect(uiState) {
        if (uiState is MoodUiState.Success) {
            onViewHistory()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mood Tracker", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onViewHistory) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = "History")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color(0xFFFBFBFF)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    "How are you feeling today?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Let's check in with your emotions",
                    color = Color.Gray,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Mood Selection Grid - Reduced height for responsiveness
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.height(200.dp),
                    userScrollEnabled = false
                ) {
                    items(MoodType.entries.toList()) { mood ->
                        MoodItemView(
                            mood = mood,
                            isSelected = selectedMood == mood,
                            onClick = { selectedMood = mood }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Intensity", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("${intensity.toInt()}%", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                
                Slider(
                    value = intensity,
                    onValueChange = { intensity = it },
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF673AB7),
                        activeTrackColor = Color(0xFF673AB7),
                        inactiveTrackColor = Color(0xFFE0E0E0)
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "Add a note (optional)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    placeholder = { Text("What's on your mind?", fontSize = 14.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedBorderColor = Color(0xFF673AB7),
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White
                    )
                )

                if (uiState is MoodUiState.Error) {
                    Text(
                        text = (uiState as MoodUiState.Error).message,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        selectedMood?.let {
                            viewModel.saveMood(it, intensity.toInt(), note)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7)),
                    enabled = uiState !is MoodUiState.Loading
                ) {
                    if (uiState is MoodUiState.Loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Save", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun MoodItemView(mood: MoodType, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) Color(0xFFF3E5F5) else Color.Transparent
    val borderColor = if (isSelected) Color(0xFF673AB7) else Color.Transparent

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .then(
                if (isSelected) Modifier.border(1.dp, borderColor, RoundedCornerShape(12.dp))
                else Modifier
            )
            .clickable { onClick() }
            .padding(10.dp)
    ) {
        Text(text = mood.toEmoji(), fontSize = 34.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = mood.name.lowercase().replaceFirstChar { it.uppercase() },
            fontSize = 13.sp,
            color = if (isSelected) Color.Black else Color.Gray
        )
    }
}

fun MoodType.toEmoji(): String = when(this) {
    MoodType.HAPPY -> "😊"
    MoodType.CALM -> "😌"
    MoodType.NEUTRAL -> "😐"
    MoodType.SAD -> "☹️"
    MoodType.ANXIOUS -> "😰"
    MoodType.ANGRY -> "😡"
}

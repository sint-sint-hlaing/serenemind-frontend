// MoodTrackerScreen.kt
package com.serenemind.ui.mood

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.serenemind.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodTrackerScreen(
    viewModel: MoodViewModel,
    isDarkMode: Boolean,
    onBack: () -> Unit = {},
    onViewHistory: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var intensity by remember { mutableFloatStateOf(40f) }
    var note by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf<MoodType?>(null) }

    // Get colors based on dark mode
    val backgroundColor = if (isDarkMode) Color(0xFF1A1A1A) else MaterialTheme.colorScheme.background
    val surfaceColor = if (isDarkMode) Color(0xFF2A2A2A) else MaterialTheme.colorScheme.surface
    val textColor = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onBackground
    val textSecondary = if (isDarkMode) Color.LightGray else MaterialTheme.colorScheme.onSurfaceVariant

    LaunchedEffect(uiState) {
        if (uiState is MoodUiState.Success) {
            onViewHistory()
            viewModel.reset()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Mood Tracker",
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
                actions = {
                    IconButton(onClick = onViewHistory) {
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = "History",
                            tint = textColor
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = if (isDarkMode) Color(0xFF1A1A1A) else Color.Transparent
                )
            )
        },
        containerColor = backgroundColor
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
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Text(
                    "Let's check in with your emotions",
                    color = textSecondary,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Mood Grid - 3 columns
                val moods = MoodType.entries.toList()
                val chunkedMoods = moods.chunked(3)

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    chunkedMoods.forEach { rowMoods ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            rowMoods.forEach { mood ->
                                Box(modifier = Modifier.weight(1f)) {
                                    MoodItemView(
                                        mood = mood,
                                        isSelected = selectedMood == mood,
                                        isDarkMode = isDarkMode,
                                        onClick = {
                                            selectedMood = if (selectedMood == mood) null else mood
                                        }
                                    )
                                }
                            }
                            // Fill empty slots in the last row if needed
                            if (rowMoods.size < 3) {
                                repeat(3 - rowMoods.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Intensity Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Intensity",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = textColor
                    )
                    Text(
                        "${intensity.toInt()}%",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Slider(
                    value = intensity,
                    onValueChange = { intensity = it },
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = if (isDarkMode) Color(0xFF333333) else Color(0xFFE0E0E0)
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Note Input
                Text(
                    "Add a note (optional)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    placeholder = {
                        Text(
                            "What's on your mind?",
                            fontSize = 14.sp,
                            color = textSecondary
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = if (isDarkMode) Color(0xFF333333) else Color(0xFFE0E0E0),
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedContainerColor = surfaceColor,
                        focusedContainerColor = surfaceColor
                    )
                )

                // Error message
                if (uiState is MoodUiState.Error) {
                    Text(
                        text = (uiState as MoodUiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Save Button
                Button(
                    onClick = {
                        selectedMood?.let {
                            viewModel.saveMood(it, intensity.toInt(), note)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedMood != null)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    ),
                    enabled = selectedMood != null && uiState !is MoodUiState.Loading
                ) {
                    if (uiState is MoodUiState.Loading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            "Save",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun MoodItemView(
    mood: MoodType,
    isSelected: Boolean,
    isDarkMode: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
    } else if (isDarkMode) {
        Color(0xFF2A2A2A)
    } else {
        Color.Transparent
    }

    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else if (isDarkMode) {
        Color(0xFF333333)
    } else {
        Color.Transparent
    }

    val textColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else if (isDarkMode) {
        Color.White
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Text(
            text = mood.toEmoji(),
            fontSize = 36.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = mood.name.lowercase().replaceFirstChar { it.uppercase() },
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = textColor
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
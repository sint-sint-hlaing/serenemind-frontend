// MeditationTimerScreen.kt
package com.serenemind.ui.meditation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeditationTimerScreen(
    viewModel: MeditationViewModel,
    isDarkMode: Boolean,
    onBack: () -> Unit,
    onTimerSet: () -> Unit = {}
) {
    val meditation by viewModel.selectedMeditation.collectAsState()
    var selectedMinutes by remember { mutableIntStateOf(5) }
    var isSettingTimer by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Preset time options
    val presetMinutes = listOf(5, 10, 15, 20, 30, 45, 60)

    // Get colors based on dark mode
    val backgroundColor = if (isDarkMode) Color(0xFF1A1A1A) else Color.White
    val surfaceColor = if (isDarkMode) Color(0xFF2A2A2A) else Color(0xFFF5F5F5)
    val textColor = if (isDarkMode) Color.White else Color(0xFF1A1A1A)
    val textSecondary = if (isDarkMode) Color.LightGray else Color(0xFF666666)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Set Timer",
                        fontWeight = FontWeight.Bold,
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
                    containerColor = backgroundColor
                )
            )
        },
        containerColor = backgroundColor
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Timer Icon
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF6C63FF).copy(alpha = 0.2f),
                                Color(0xFFFF6B6B).copy(alpha = 0.2f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Timer,
                    contentDescription = "Timer",
                    modifier = Modifier.size(48.dp),
                    tint = Color(0xFF6C63FF)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Title
            Text(
                text = if (meditation != null) "Set timer for ${meditation?.title ?: "Meditation"}" else "Set Meditation Timer",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = textColor,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "End meditation in",
                fontSize = 14.sp,
                color = textSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Timer Display
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF6C63FF).copy(alpha = 0.1f),
                                Color(0xFFFF6B6B).copy(alpha = 0.1f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$selectedMinutes",
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6C63FF)
                    )
                    Text(
                        text = "minutes",
                        fontSize = 16.sp,
                        color = textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Preset Time Chips
            Text(
                text = "Quick Select",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = textColor,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presetMinutes.chunked(4).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        row.forEach { minutes ->
                            FilterChip(
                                selected = selectedMinutes == minutes,
                                onClick = { selectedMinutes = minutes },
                                label = {
                                    Text(
                                        "$minutes min",
                                        fontSize = 12.sp,
                                        fontWeight = if (selectedMinutes == minutes) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF6C63FF),
                                    selectedLabelColor = Color.White,
                                    containerColor = surfaceColor,
                                    labelColor = textColor
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Slider
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("1 min", fontSize = 12.sp, color = textSecondary)
                    Text("60 min", fontSize = 12.sp, color = textSecondary)
                }

                Slider(
                    value = selectedMinutes.toFloat(),
                    onValueChange = { selectedMinutes = it.toInt() },
                    valueRange = 1f..60f,
                    steps = 59,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF6C63FF),
                        activeTrackColor = Color(0xFF6C63FF),
                        inactiveTrackColor = if (isDarkMode) Color(0xFF333333) else Color(0xFFE0E0E0)
                    )
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Set Timer Button
            Button(
                onClick = {
                    meditation?.let {
                        isSettingTimer = true
                        viewModel.saveTimer(it.id, selectedMinutes) { msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            isSettingTimer = false
                            onTimerSet()
                            onBack()
                        }
                    } ?: run {
                        Toast.makeText(context, "No meditation selected", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = !isSettingTimer,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6C63FF)
                )
            ) {
                if (isSettingTimer) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Setting Timer...",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.AccessTime,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Set Timer",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Cancel button
            TextButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Cancel",
                    color = textSecondary,
                    fontSize = 14.sp
                )
            }
        }
    }
}
// MeditationTimerScreen.kt
package com.serenemind.ui.meditation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.serenemind.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MeditationTimerScreen(
    viewModel: MeditationViewModel,
    isDarkMode: Boolean,
    onBack: () -> Unit,
    onTimerSet: () -> Unit = {}
) {
    val meditation by viewModel.selectedMeditation.collectAsState()
    val timerUiState by viewModel.timerUiState.collectAsState()
    var selectedMinutes by remember { mutableIntStateOf(timerUiState.selectedMinutes) }
    val context = LocalContext.current

    // Preset time options
    val presetMinutes = listOf(5, 10, 15, 20, 30)

    // Get colors based on dark mode
    val backgroundColor = if (isDarkMode) Color(0xFF1A1A1A) else Color.White
    val surfaceColor = if (isDarkMode) Color(0xFF2A2A2A) else Color(0xFFF5F5F5)
    val textColor = if (isDarkMode) Color.White else TextPrimary
    val textSecondary = if (isDarkMode) Color.LightGray else TextSecondary

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Set Meditation Timer",
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
                .padding(horizontal = 24.dp)
        ) {
            // Scrollable Content area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                
                // Timer Icon & Display
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Timer,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = String.format("%02d:00", selectedMinutes),
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Title
                Text(
                    text = meditation?.title ?: "Select duration for your session",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textColor,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "$selectedMinutes minutes meditation",
                    fontSize = 14.sp,
                    color = textSecondary
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Quick Select Section
                Text(
                    text = "Quick Select",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    presetMinutes.forEach { minutes ->
                        FilterChip(
                            selected = selectedMinutes == minutes,
                            onClick = { selectedMinutes = minutes },
                            label = {
                                Text(
                                    "$minutes min",
                                    fontSize = 13.sp,
                                    fontWeight = if (selectedMinutes == minutes) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White,
                                containerColor = surfaceColor,
                                labelColor = textColor
                            ),
                            border = null
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Slider Section
                Text(
                    text = "Custom Duration",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))

                Slider(
                    value = selectedMinutes.toFloat(),
                    onValueChange = { selectedMinutes = it.toInt() },
                    valueRange = 1f..60f,
                    steps = 59,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = if (isDarkMode) Color(0xFF333333) else Color(0xFFE0E0E0)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("1 min", fontSize = 12.sp, color = textSecondary)
                    Text("60 min", fontSize = 12.sp, color = textSecondary)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

            // Bottom Fixed Actions (Primary Action)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (timerUiState.error != null) {
                    Text(
                        text = timerUiState.error!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                Button(
                    onClick = {
                        meditation?.let {
                            viewModel.saveTimer(it.id, selectedMinutes) { msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
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
                    shape = RoundedCornerShape(28.dp),
                    enabled = !timerUiState.isSaving,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    if (timerUiState.isSaving) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 3.dp
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.AccessTime,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "START TIMER",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                letterSpacing = 1.25.sp
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
                        "CANCEL",
                        color = textSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
                
                // Add padding for bottom navigation bar if it's visible
                Spacer(modifier = Modifier.navigationBarsPadding())
            }
        }
    }
}

package com.serenemind.ui.goal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.serenemind.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProgressScreen(
    viewModel: GoalViewModel,
    isDarkMode: Boolean,
    onBack: () -> Unit = {}
) {
    val goal by viewModel.selectedGoal.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    
    var isCompleted by remember { mutableStateOf(true) }
    var note by remember { mutableStateOf("") }

    val backgroundColor = if (isDarkMode) Color(0xFF1A1A1A) else Color.White
    val surfaceColor = if (isDarkMode) Color(0xFF2A2A2A) else Color(0xFFF9F9F9)
    val textPrimary = if (isDarkMode) Color.White else TextPrimary
    val textSecondary = if (isDarkMode) Color.LightGray else TextSecondary

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Update Progress", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        containerColor = backgroundColor
    ) { padding ->
        goal?.let { g ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                // Goal Card
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = surfaceColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Success.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Success)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(g.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textPrimary)
                            Text("${g.progress} / ${g.targetDays} days", fontSize = 13.sp, color = textSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text("Mark Today", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textPrimary)
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Completed Button
                    MarkItem(
                        label = "Completed",
                        isSelected = isCompleted,
                        selectedColor = Success,
                        icon = Icons.Default.Check,
                        onClick = { isCompleted = true },
                        isDarkMode = isDarkMode
                    )

                    // Skipped Button
                    MarkItem(
                        label = "Skipped",
                        isSelected = !isCompleted,
                        selectedColor = Color.Gray,
                        icon = Icons.Default.Close,
                        onClick = { isCompleted = false },
                        isDarkMode = isDarkMode
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                Text("Add Note (Optional)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = textPrimary)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = { if (it.length <= 100) note = it },
                    placeholder = { Text("How did it go today?", fontSize = 14.sp) },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = surfaceColor,
                        focusedContainerColor = surfaceColor
                    )
                )
                Text(
                    text = "${note.length}/100",
                    fontSize = 12.sp,
                    color = textSecondary,
                    modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        viewModel.updateProgress(g.id, isCompleted, note) {
                            onBack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    enabled = uiState !is GoalUiState.Loading
                ) {
                    if (uiState is GoalUiState.Loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Save", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun MarkItem(
    label: String,
    isSelected: Boolean,
    selectedColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    isDarkMode: Boolean
) {
    val textPrimary = if (isDarkMode) Color.White else TextPrimary
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(if (isSelected) selectedColor else if (isDarkMode) Color(0xFF333333) else Color(0xFFF5F5F5))
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(32.dp),
                tint = if (isSelected) Color.White else Color.Gray
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(label, fontSize = 12.sp, color = if (isSelected) textPrimary else Color.Gray, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
    }
}

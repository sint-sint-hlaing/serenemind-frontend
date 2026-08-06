package com.serenemind.ui.goal

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.serenemind.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalScreen(
    viewModel: GoalViewModel,
    onBack: () -> Unit = {},
    onSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val createGoalSuccess by viewModel.createGoalSuccess.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current
    
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var target by remember { mutableIntStateOf(10) }
    var unit by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("Daily") }
    var startDate by remember { mutableStateOf("May 12, 2024") }
    var reminderTime by remember { mutableStateOf("9:00 PM") }

    var showFrequencyMenu by remember { mutableStateOf(false) }
    val frequencies = listOf("Daily", "Weekly", "Monthly")

    LaunchedEffect(createGoalSuccess) {
        if (createGoalSuccess) {
            viewModel.resetCreateGoalSuccess()
            onSuccess()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("New Goal", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = {
                        if (title.isBlank()) {
                            android.widget.Toast.makeText(context, "Please enter a title", android.widget.Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.createGoal(title, description, target)
                        }
                    }) {
                        Text("Save", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon Picker
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    .clickable { 
                        android.widget.Toast.makeText(context, "Icon picker coming soon", android.widget.Toast.LENGTH_SHORT).show()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.SelfImprovement,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Change Icon", fontSize = 13.sp, color = TextSecondary)

            Spacer(modifier = Modifier.height(32.dp))

            // Form Fields
            GoalInputField(label = "Goal Title", value = title, onValueChange = { title = it }, placeholder = "e.g. Read 20 pages daily")
            Spacer(modifier = Modifier.height(20.dp))
            GoalInputField(label = "Description (optional)", value = description, onValueChange = { description = it }, placeholder = "Why is this goal important to you?")
            
            Spacer(modifier = Modifier.height(24.dp))

            // Frequency
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Frequency", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                Box {
                    OutlinedCard(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showFrequencyMenu = true },
                        colors = CardDefaults.outlinedCardColors(containerColor = Color(0xFFF9F9F9)),
                        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(frequency, color = TextPrimary, fontSize = 15.sp)
                            Icon(Icons.Default.Add, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
                        }
                    }
                    DropdownMenu(
                        expanded = showFrequencyMenu,
                        onDismissRequest = { showFrequencyMenu = false }
                    ) {
                        frequencies.forEach { f ->
                            DropdownMenuItem(
                                text = { Text(f) },
                                onClick = {
                                    frequency = f
                                    showFrequencyMenu = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Target & Unit
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Target", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(Color(0xFFF9F9F9))
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { if (target > 1) target-- },
                            modifier = Modifier.size(32.dp).clip(CircleShape).background(Color.White)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextSecondary)
                        }
                        Text(
                            target.toString(), 
                            modifier = Modifier.weight(1f), 
                            textAlign = TextAlign.Center, 
                            fontWeight = FontWeight.Bold, 
                            fontSize = 16.sp
                        )
                        IconButton(
                            onClick = { target++ },
                            modifier = Modifier.size(32.dp).clip(CircleShape).background(Color.White)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextSecondary)
                        }
                    }
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Text("Unit", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        placeholder = { Text("e.g. pages", fontSize = 14.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFEEEEEE),
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedContainerColor = Color(0xFFF9F9F9),
                            focusedContainerColor = Color(0xFFF9F9F9)
                        ),
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Date & Reminder
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Start Date", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF9F9F9))
                            .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
                            .clickable {
                                android.widget.Toast.makeText(context, "Date picker coming soon", android.widget.Toast.LENGTH_SHORT).show()
                            }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(startDate, fontSize = 14.sp, color = TextPrimary)
                        Icon(Icons.Outlined.CalendarToday, contentDescription = null, modifier = Modifier.size(18.dp), tint = TextSecondary)
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Reminder", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF9F9F9))
                            .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
                            .clickable {
                                android.widget.Toast.makeText(context, "Reminder picker coming soon", android.widget.Toast.LENGTH_SHORT).show()
                            }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(reminderTime, fontSize = 14.sp, color = TextPrimary)
                        Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(18.dp), tint = TextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun GoalInputField(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, fontSize = 14.sp) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFEEEEEE),
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedContainerColor = Color(0xFFF9F9F9),
                focusedContainerColor = Color(0xFFF9F9F9)
            )
        )
    }
}

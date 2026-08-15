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
import com.serenemind.model.entity.enums.Frequency
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalScreen(
    viewModel: GoalViewModel,
    isDarkMode: Boolean,
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
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

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
                    TextButton(
                        onClick = {
                            if (title.isBlank()) {
                                android.widget.Toast.makeText(context, "Please enter a title", android.widget.Toast.LENGTH_SHORT).show()
                            } else {
                                val frequencyEnum = when (frequency) {
                                    "Daily" -> Frequency.DAILY
                                    "Weekly" -> Frequency.WEEKLY
                                    "Monthly" -> Frequency.MONTHLY
                                    else -> Frequency.DAILY
                                }
                                viewModel.createGoal(title, description, target, frequencyEnum)
                            }
                        }
                    ) {
                        Text("Save", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background               )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
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
            GoalInputField(label = "Goal Title", value = title, onValueChange = { title = it }, placeholder = "e.g. Read 20 pages daily", isDarkMode = isDarkMode)
            Spacer(modifier = Modifier.height(20.dp))
            GoalInputField(label = "Description (optional)", value = description, onValueChange = { description = it }, placeholder = "Why is this goal important to you?", isDarkMode = isDarkMode)
            
            Spacer(modifier = Modifier.height(24.dp))

            // Frequency
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Frequency", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Box {
                    OutlinedCard(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showFrequencyMenu = true },
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = if (isDarkMode) Color(0xFF2A2A2A) else Color(0xFFF9F9F9)
                        ),
                        border = BorderStroke(1.dp, if (isDarkMode) Color(0xFF333333) else Color(0xFFEEEEEE))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(frequency, fontSize = 15.sp)
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
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
                    Text("Target", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(if (isDarkMode) Color(0xFF2A2A2A) else Color(0xFFF9F9F9))
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { if (target > 1) target-- },
                            modifier = Modifier.size(32.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surface)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
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
                            modifier = Modifier.size(32.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surface)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Text("Unit", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        placeholder = { Text("e.g. pages", fontSize = 14.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = if (isDarkMode) Color(0xFF333333) else Color(0xFFEEEEEE),
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedContainerColor = if (isDarkMode) Color(0xFF2A2A2A) else Color(0xFFF9F9F9),
                            focusedContainerColor = if (isDarkMode) Color(0xFF2A2A2A) else Color(0xFFF9F9F9)
                        ),
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Date
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Start Date", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDarkMode) Color(0xFF2A2A2A) else Color(0xFFF9F9F9))
                        .border(1.dp, if (isDarkMode) Color(0xFF333333) else Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
                        .clickable { showDatePicker = true }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(startDate, fontSize = 14.sp)
                    Icon(Icons.Outlined.CalendarToday, contentDescription = null, modifier = Modifier.size(18.dp))
                }
            }

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val date = Date(millis)
                                val formatter = SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH)
                                startDate = formatter.format(date)
                            }
                            showDatePicker = false
                        }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Choose Color
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Choose Color", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                val colors = listOf(
                    Color(0xFF6C63FF), Color(0xFF4CAF50), Color(0xFF03A9F4), 
                    Color(0xFFFF9800), Color(0xFFE53935), Color(0xFFE91E63)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    colors.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(color)
                                .clickable { /* Select color */ }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun GoalInputField(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String, isDarkMode: Boolean = false) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, fontSize = 14.sp) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = if (isDarkMode) Color(0xFF333333) else Color(0xFFEEEEEE),
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedContainerColor = if (isDarkMode) Color(0xFF2A2A2A) else Color(0xFFF9F9F9),
                focusedContainerColor = if (isDarkMode) Color(0xFF2A2A2A) else Color(0xFFF9F9F9)
            )
        )
    }
}

package com.serenemind.ui.profile

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderScreen(
    viewModel: ReminderViewModel,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val uiState by viewModel.addUiState.collectAsState()
    val context = LocalContext.current
    
    var title by remember { mutableStateOf("") }
    var repeatType by remember { mutableStateOf("Daily") }
    var timeDisplay by remember { mutableStateOf("09:00 AM") }
    var timeValue by remember { mutableStateOf("09:00:00") }
    
    val calendar = Calendar.getInstance()
    val sdfDateDisplay = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val sdfDateValue = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val sdfTimeDisplay = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val sdfTimeValue = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

    var startDateDisplay by remember { mutableStateOf(sdfDateDisplay.format(calendar.time)) }
    var startDateValue by remember { mutableStateOf(sdfDateValue.format(calendar.time)) }
    var tone by remember { mutableStateOf("Gentle Bell") }
    var note by remember { mutableStateOf("") }
    var enabled by remember { mutableStateOf(true) }

    val repeatOptions = listOf("Daily", "Weekly", "Monthly", "Mon, Wed, Fri")
    val toneOptions = listOf("Gentle Bell", "Bird Chirping", "Zen Gong", "Soft Piano")

    // Date Picker Dialog
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)
            startDateDisplay = sdfDateDisplay.format(selectedDate.time)
            startDateValue = sdfDateValue.format(selectedDate.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // Time Picker Dialog
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            val selectedTime = Calendar.getInstance()
            selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
            selectedTime.set(Calendar.MINUTE, minute)
            timeDisplay = sdfTimeDisplay.format(selectedTime.time)
            timeValue = sdfTimeValue.format(selectedTime.time)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        false
    )

    LaunchedEffect(uiState) {
        if (uiState is AddReminderUiState.Success) {
            onSaveSuccess()
            viewModel.resetAddState()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("New Reminder", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back", modifier = Modifier.size(20.dp))
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            viewModel.createReminder(
                                context = context,
                                title = title,
                                repeatType = repeatType.uppercase(),
                                time = timeValue,
                                startDate = startDateValue,
                                tone = tone,
                                note = note,
                                enabled = enabled
                            )
                        },
                        enabled = title.isNotBlank() && uiState !is AddReminderUiState.Loading
                    ) {
                        if (uiState is AddReminderUiState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Save", color = if (title.isNotBlank()) Color(0xFF6750A4) else Color.Gray, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Title", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("e.g. Take a deep breath", color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFEEEEEE),
                    focusedBorderColor = Color(0xFF6750A4)
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            DropdownField(label = "Repeat", options = repeatOptions, selectedOption = repeatType) { repeatType = it }
            
            Spacer(modifier = Modifier.height(24.dp))

            Text("Time", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = timeDisplay,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth().clickable { timePickerDialog.show() },
                shape = RoundedCornerShape(12.dp),
                trailingIcon = { Icon(Icons.Default.Schedule, contentDescription = null, tint = Color.Gray) },
                readOnly = true,
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = Color(0xFFEEEEEE),
                    disabledTextColor = Color.Black,
                    disabledTrailingIconColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("Start Date", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = startDateDisplay,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth().clickable { datePickerDialog.show() },
                shape = RoundedCornerShape(12.dp),
                trailingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color.Gray) },
                readOnly = true,
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = Color(0xFFEEEEEE),
                    disabledTextColor = Color.Black,
                    disabledTrailingIconColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            DropdownField(label = "Reminder Tone", options = toneOptions, selectedOption = tone) { tone = it }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Note (optional)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                placeholder = { Text("Add a note...", color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFEEEEEE),
                    focusedBorderColor = Color(0xFF6750A4)
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Enable Notification", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Switch(
                    checked = enabled,
                    onCheckedChange = { enabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF6750A4)
                    )
                )
            }

            if (uiState is AddReminderUiState.Error) {
                Text((uiState as AddReminderUiState.Error).message, color = Color.Red, fontSize = 12.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(label, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Box {
            OutlinedTextField(
                value = selectedOption,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.Gray) },
                readOnly = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFEEEEEE),
                    focusedBorderColor = Color(0xFFEEEEEE)
                )
            )
            // Invisible box over field to trigger dropdown
            Box(modifier = Modifier.matchParentSize().clickable { expanded = true })
            
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

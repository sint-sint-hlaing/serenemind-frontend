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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
    
    val isoFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val displayFormatter = remember { SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH) }
    
    var startDateIso by remember { mutableStateOf(isoFormatter.format(Date())) }
    var selectedCategory by remember { mutableStateOf("Meditation") }

    val categories = listOf(
        "Meditation" to Icons.Outlined.SelfImprovement,
        "Journal" to Icons.AutoMirrored.Outlined.Assignment,
        "Water" to Icons.Outlined.LocalDrink,
        "Sleep" to Icons.Outlined.Nightlight,
        "Exercise" to Icons.Outlined.DirectionsRun,
        "Reading" to Icons.Outlined.MenuBook,
        "Study" to Icons.Outlined.School,
        "Health" to Icons.Outlined.Favorite,
        "Other" to Icons.Outlined.CheckCircle
    )

    val categoryColorMap = remember {
        mapOf(
            "Meditation" to "purple",
            "Journal"    to "green",
            "Water"      to "blue",
            "Sleep"      to "orange",
            "Exercise"   to "red",
            "Reading"    to "pink",
            "Study"      to "indigo",
            "Health"     to "cyan",
            "Other"      to "grey"
        )
    }

    val selectedColorName = categoryColorMap[selectedCategory] ?: "purple"

    val currentColor = when (selectedColorName) {
        "red"      -> Color(0xFFE53935)
        "blue"     -> Color(0xFF1E88E5)
        "green"    -> Color(0xFF43A047)
        "orange"   -> Color(0xFFFB8C00)
        "pink"     -> Color(0xFFD81B60)
        "purple"   -> Color(0xFF8E24AA)
        "indigo"   -> Color(0xFF3949AB)
        "cyan"     -> Color(0xFF00ACC1)
        "grey"     -> Color(0xFF757575)
        else       -> MaterialTheme.colorScheme.primary
    }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

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
                    if (uiState is GoalUiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                    } else {
                        TextButton(
                            onClick = {
                                if (title.isBlank()) {
                                    android.widget.Toast.makeText(context, "Please enter a title", android.widget.Toast.LENGTH_SHORT).show()
                                } else {
                                    viewModel.createGoal(
                                        title = title,
                                        description = description,
                                        targetDays = target,
                                        unit = "days",
                                        frequency = Frequency.DAILY,
                                        color = selectedColorName,
                                        icon = selectedCategory,
                                        startDate = startDateIso
                                    )
                                }
                            }
                        ) {
                            Text("Save", color = currentColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
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
                .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Category Picker
            Column(modifier = Modifier.padding(horizontal = 24.dp).padding(top = 16.dp)) {
                Text("Select Category", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyRow(
                    contentPadding = PaddingValues(end = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { (name, icon) ->
                        val isSelected = selectedCategory == name
                        CategoryItem(
                            name = name,
                            icon = icon,
                            isSelected = isSelected,
                            selectedColor = currentColor,
                            isDarkMode = isDarkMode,
                            onClick = { selectedCategory = name }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                // Form Fields
                GoalInputField(label = "Goal Title", value = title, onValueChange = { title = it }, placeholder = "e.g. Read 20 pages daily", isDarkMode = isDarkMode, focusedColor = currentColor)
                Spacer(modifier = Modifier.height(20.dp))
                GoalInputField(label = "Description (optional)", value = description, onValueChange = { description = it }, placeholder = "Why is this goal important to you?", isDarkMode = isDarkMode, focusedColor = currentColor)
            
                Spacer(modifier = Modifier.height(24.dp))

                // Target Days
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Target Days", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
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
                        val displayDate = try {
                            val date = isoFormatter.parse(startDateIso)
                            displayFormatter.format(date!!)
                        } catch (e: Exception) {
                            startDateIso
                        }
                        Text(displayDate, fontSize = 14.sp)
                        Icon(Icons.Outlined.CalendarToday, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }

                if (showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    startDateIso = isoFormatter.format(Date(millis))
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

                Spacer(modifier = Modifier.height(32.dp))

                if (uiState is GoalUiState.Error) {
                    Text(
                        text = (uiState as GoalUiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    )
                }
            } // End of Form Fields Column
        }
    }
}

@Composable
fun CategoryItem(
    name: String,
    icon: ImageVector,
    isSelected: Boolean,
    selectedColor: Color,
    isDarkMode: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(72.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (isSelected) selectedColor.copy(alpha = 0.9f)
                    else if (isDarkMode) Color(0xFF2A2A2A)
                    else Color(0xFFF5F5F5)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = name,
                tint = if (isSelected) {
                    if (name == "Reading" || name == "Water" || name == "Sleep" || name == "Exercise" || name == "Health" || name == "Journal" || name == "Meditation" || name == "Study" || name == "Other") {
                         // Most colors look good with white, yellow/pink might need black but pink hex is dark enough
                         Color.White
                    } else Color.White
                }
                else if (isDarkMode) Color.LightGray
                else Color.DarkGray,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = name,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) selectedColor else TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun GoalInputField(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String, isDarkMode: Boolean = false, focusedColor: Color = MaterialTheme.colorScheme.primary) {
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
                focusedBorderColor = focusedColor,
                unfocusedContainerColor = if (isDarkMode) Color(0xFF2A2A2A) else Color(0xFFF9F9F9),
                focusedContainerColor = if (isDarkMode) Color(0xFF2A2A2A) else Color(0xFFF9F9F9)
            )
        )
    }
}

// GoalScreen.kt (Improved)
package com.serenemind.ui.goal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DirectionsRun
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.LocalDrink
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Nightlight
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.SelfImprovement
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
import com.serenemind.model.entity.enums.GoalStatus
import com.serenemind.model.response.GoalResponse
import com.serenemind.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalScreen(
    viewModel: GoalViewModel,
    isDarkMode: Boolean,
    onGoalClick: (GoalResponse) -> Unit = {},
    onAddGoalClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) } // 0: All, 1: Active, 2: Completed
    val context = androidx.compose.ui.platform.LocalContext.current
    var goalToDelete by remember { mutableStateOf<GoalResponse?>(null) }

    if (goalToDelete != null) {
        AlertDialog(
            onDismissRequest = { goalToDelete = null },
            title = { Text("Delete Goal") },
            text = { Text("Are you sure you want to permanently delete this goal? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        goalToDelete?.id?.let { viewModel.hardDeleteGoal(it) }
                        goalToDelete = null
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { goalToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "My Goals",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        // TODO: Open navigation drawer or menu
                        android.widget.Toast.makeText(context, "Opening menu...", android.widget.Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onAddGoalClick) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add",
                            tint = if (isDarkMode) Color.White else MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = if (isDarkMode) Color(0xFF1A1A1A) else MaterialTheme.colorScheme.surface      )
            )
        },
        containerColor = if (isDarkMode) Color(0xFF1A1A1A) else MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                GoalTab("All", selectedTab == 0, isDarkMode) { selectedTab = 0 }
                Spacer(modifier = Modifier.width(12.dp))
                GoalTab("Active", selectedTab == 1, isDarkMode) { selectedTab = 1 }
                Spacer(modifier = Modifier.width(12.dp))
                GoalTab("Completed", selectedTab == 2, isDarkMode) { selectedTab = 2 }
            }

            when (val state = uiState) {
                is GoalUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Loading goals...",
                                color = if (isDarkMode) Color.LightGray else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                is GoalUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "❌",
                                fontSize = 48.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = state.message,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.fetchGoals() }) {
                                Text("Retry")
                            }
                        }
                    }
                }
                is GoalUiState.Success -> {
                    val filteredGoals = when (selectedTab) {
                        0 -> state.goals // All goals
                        1 -> state.goals.filter {
                            it.status == "ACTIVE" || it.status == "PAUSED"
                        }
                        2 -> state.goals.filter { it.status == "COMPLETED" }
                        else -> state.goals
                    }

                    if (filteredGoals.isEmpty()) {
                        EmptyGoalsState(
                            onAddClick = onAddGoalClick,
                            isDarkMode = isDarkMode,
                            selectedTab = selectedTab
                        )
                    } else {
                        GoalList(
                            goals = filteredGoals,
                            isDarkMode = isDarkMode,
                            onGoalClick = { goal ->
                                onGoalClick(goal)
                            },
                            onDeleteGoal = { goal ->
                                goalToDelete = goal
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyGoalsState(
    onAddClick: () -> Unit,
    isDarkMode: Boolean = false,
    selectedTab: Int = 0
) {
    val textColor = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onBackground
    val textSecondary = if (isDarkMode) Color.LightGray else MaterialTheme.colorScheme.onSurfaceVariant

    // Different messages based on tab
    val (emoji, title, description) = when (selectedTab) {
        1 -> Triple("🏃", "No Active Goals", "You don't have any active goals right now. Start a new one!")
        2 -> Triple("🎉", "No Completed Goals", "Complete a goal to see it here. Keep going!")
        else -> Triple("📭", "No Goals Found", "Start building healthy habits by creating your first goal.")
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                emoji,
                fontSize = 64.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                description,
                textAlign = TextAlign.Center,
                color = textSecondary,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onAddClick,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Create Goal", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun GoalTab(
    title: String,
    isSelected: Boolean,
    isDarkMode: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isDarkMode -> Color(0xFF2A2A2A)
        else -> Color(0xFFF5F5F5)
    }

    val textColor = when {
        isSelected -> Color.White
        isDarkMode -> Color.LightGray
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        modifier = Modifier.height(34.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 18.dp)
        ) {
            Text(
                text = title,
                color = textColor,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
fun GoalList(
    goals: List<GoalResponse>,
    isDarkMode: Boolean,
    onGoalClick: (GoalResponse) -> Unit,
    onDeleteGoal: (GoalResponse) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = goals,
            key = { it.id ?: 0 } // Add key for better performance
        ) { goal ->
            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = {
                    if (it == SwipeToDismissBoxValue.EndToStart) {
                        onDeleteGoal(goal)
                        false // Return false so it snaps back, dialog will handle deletion
                    } else {
                        false
                    }
                }
            )

            SwipeToDismissBox(
                state = dismissState,
                backgroundContent = {
                    val color = when (dismissState.dismissDirection) {
                        SwipeToDismissBoxValue.EndToStart -> Color.Red.copy(alpha = 0.8f)
                        else -> Color.Transparent
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(24.dp))
                            .background(color)
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.White
                        )
                    }
                },
                enableDismissFromStartToEnd = false
            ) {
                GoalItem(
                    goal = goal,
                    isDarkMode = isDarkMode,
                    onClick = onGoalClick
                )
            }
        }
    }
}

@Composable
fun GoalItem(
    goal: GoalResponse,
    isDarkMode: Boolean,
    onClick: (GoalResponse) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(goal) },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) Color(0xFF2A2A2A) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(getGoalIconBgColor(goal.icon ?: goal.title ?: "", isDarkMode, goal.color)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    getGoalIcon(goal.icon, goal.title ?: ""),
                    contentDescription = null,
                    tint = getGoalIconColor(goal.icon ?: goal.title ?: "", goal.color),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(18.dp))

            // Content
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        goal.title?:"Untitled",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                        maxLines = 1
                    )

                    // Status badge
                    when (goal.status) {
                        "PAUSED" -> {
                            Text("⏸️", fontSize = 14.sp)
                        }
                        "EXPIRED" -> {
                            Text("⏰", fontSize = 14.sp)
                        }
                        else -> {}
                    }
                }

                Text(
                    "${goal.progress} / ${goal.targetDays?:0} days",
                    color = if (isDarkMode) Color.LightGray else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Progress bar
                val progress = (goal.progress?:0).toFloat() / (goal.targetDays?:1).toFloat().coerceAtLeast(1f)
                LinearProgressIndicator(
                    progress = { progress.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    color = getGoalIconColor(goal.title ?: "", goal.color),
                    trackColor = if (isDarkMode) Color(0xFF333333) else Color(0xFFF5F5F5),
                )
            }
        }
    }
}

// ===== Icon Helper Functions =====

fun getGoalIcon(iconName: String?, title: String): ImageVector {
    val name = iconName?.lowercase() ?: title.lowercase()
    return when {
        name.contains("meditation") -> Icons.Outlined.SelfImprovement
        name.contains("journal") -> Icons.AutoMirrored.Outlined.Assignment
        name.contains("water") -> Icons.Outlined.LocalDrink
        name.contains("sleep") -> Icons.Outlined.Nightlight
        name.contains("exercise") -> Icons.Outlined.DirectionsRun
        name.contains("run") -> Icons.Outlined.DirectionsRun
        name.contains("read") -> Icons.Outlined.MenuBook
        name.contains("study") -> Icons.Outlined.School
        name.contains("health") -> Icons.Outlined.Favorite
        name.contains("favorite") -> Icons.Outlined.Favorite
        name.contains("ai") -> Icons.Outlined.AutoAwesome
        name.contains("chat") -> Icons.Outlined.AutoAwesome
        name.contains("awesome") -> Icons.Outlined.AutoAwesome
        else -> Icons.Outlined.CheckCircle
    }
}

fun getGoalIconBgColor(name: String, isDarkMode: Boolean, colorName: String? = null): Color {
    if (colorName != null) {
        val color = getGoalIconColor("", colorName)
        return if (isDarkMode) color.copy(alpha = 0.2f) else color.copy(alpha = 0.15f)
    }
    val n = name.lowercase()
    val baseColor = when {
        n.contains("meditation") -> Color(0xFFE8EAF6)
        n.contains("journal") -> Color(0xFFE8F5E9)
        n.contains("water") -> Color(0xFFE1F5FE)
        n.contains("sleep") -> Color(0xFFFFF3E0)
        n.contains("exercise") -> Color(0xFFFFEBEE)
        n.contains("read") -> Color(0xFFFCE4EC)
        n.contains("study") -> Color(0xFFE8EAF6)
        else -> Color(0xFFF5F5F5)
    }
    return if (isDarkMode) baseColor.copy(alpha = 0.2f) else baseColor
}

fun getGoalIconColor(name: String, colorName: String? = null): Color {
    if (colorName != null) {
        return when (colorName.lowercase()) {
            "red" -> Color(0xFFE53935)
            "blue" -> Color(0xFF1E88E5)
            "green" -> Color(0xFF43A047)
            "yellow" -> Color(0xFFFBC02D) // Darker yellow for better visibility
            "purple" -> Color(0xFF8E24AA)
            "orange" -> Color(0xFFFB8C00)
            else -> MoodHappy
        }
    }
    val n = name.lowercase()
    return when {
        n.contains("meditation") -> Color(0xFF7C4DFF)
        n.contains("journal") -> Color(0xFF4CAF50)
        n.contains("water") -> Color(0xFF03A9F4)
        n.contains("sleep") -> Color(0xFFFF9800)
        n.contains("exercise") -> Color(0xFFE53935)
        n.contains("read") -> Color(0xFFE91E63)
        n.contains("study") -> Color(0xFF3F51B5)
        else -> MoodHappy
    }
}
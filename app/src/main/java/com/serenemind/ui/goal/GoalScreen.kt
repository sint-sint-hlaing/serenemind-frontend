package com.serenemind.ui.goal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.serenemind.model.response.UserGoal
import com.serenemind.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalScreen(
    viewModel: GoalViewModel,
    onGoalClick: (UserGoal) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(1) } // 0: All, 1: Active, 2: Completed

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Goals", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { /* Open menu */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Add goal */ }) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                GoalTab("All", selectedTab == 0) { selectedTab = 0 }
                Spacer(modifier = Modifier.width(12.dp))
                GoalTab("Active", selectedTab == 1) { selectedTab = 1 }
                Spacer(modifier = Modifier.width(12.dp))
                GoalTab("Completed", selectedTab == 2) { selectedTab = 2 }
            }

            when (val state = uiState) {
                is GoalUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                is GoalUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
                is GoalUiState.Success -> {
                    val filteredGoals = when (selectedTab) {
                        0 -> state.goals
                        1 -> state.goals.filter { it.progress < it.targetDays }
                        2 -> state.goals.filter { it.progress >= it.targetDays }
                        else -> state.goals
                    }
                    GoalList(filteredGoals, onGoalClick)
                }
            }
        }
    }
}


@Composable
fun GoalTab(title: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFF5F5F5),
        modifier = Modifier.height(34.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 18.dp)) {
            Text(
                text = title,
                color = if (isSelected) Color.White else TextSecondary,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
fun GoalList(goals: List<UserGoal>, onGoalClick: (UserGoal) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(goals) { goal ->
            GoalItem(goal, onGoalClick)
        }
    }
}

@Composable
fun GoalItem(goal: UserGoal, onClick: (UserGoal) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(goal) },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(getGoalIconBgColor(goal.id)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = getGoalIconColor(goal.id),
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(18.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(goal.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                Text("${goal.progress} / ${goal.targetDays} days", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { goal.progress.toFloat() / goal.targetDays.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    color = getGoalIconColor(goal.id),
                    trackColor = Color(0xFFF5F5F5),
                )
            }
        }
    }
}

fun getGoalIconBgColor(id: Long): Color {
    return when (id % 4) {
        0L -> ActionJournal
        1L -> ActionGoals
        2L -> ActionMeditation
        else -> ActionBreathing
    }
}

fun getGoalIconColor(id: Long): Color {
    return when (id % 4) {
        0L -> MoodSad
        1L -> MoodCalm
        2L -> MoodNeutral
        else -> MoodHappy
    }
}

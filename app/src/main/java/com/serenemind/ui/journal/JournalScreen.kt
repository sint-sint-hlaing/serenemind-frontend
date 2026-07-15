package com.serenemind.ui.journal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
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
fun JournalScreen(
    onAddClick: () -> Unit = {},
    onJournalClick: (Long) -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Journal", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { /* Open menu */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Search */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = onAddClick) {
                        Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(28.dp))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Filter Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                JournalFilterTab("All", true)
                Spacer(modifier = Modifier.width(12.dp))
                JournalFilterTab("Favorites", false)
                Spacer(modifier = Modifier.width(12.dp))
                JournalFilterTab("Tagged", false)
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    JournalEntryItem(
                        title = "A better day",
                        preview = "Today was a good day. I finished my tasks and spent time with my family in the evening which made me feel so relaxed and happy.",
                        date = "May 12, 2024",
                        onClick = { onJournalClick(1L) }
                    )
                }
                item {
                    JournalEntryItem(
                        title = "Grateful for little things",
                        preview = "I am grateful for my family, friends and good health. Things are getting better. I just need to keep going.",
                        date = "May 10, 2024",
                        onClick = { onJournalClick(2L) }
                    )
                }
                item {
                    JournalEntryItem(
                        title = "Overthinking",
                        preview = "Sometimes, I think too much about the future... but I should focus on the present moment.",
                        date = "May 08, 2024",
                        onClick = { onJournalClick(3L) }
                    )
                }
                item {
                    JournalEntryItem(
                        title = "New beginnings",
                        preview = "Excited for what's coming next. I will do my best to stay positive.",
                        date = "May 05, 2024",
                        onClick = { onJournalClick(4L) }
                    )
                }
            }
        }
    }
}

@Composable
fun JournalFilterTab(title: String, isSelected: Boolean) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFF5F5F5),
        modifier = Modifier.height(34.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 18.dp)) {
            Text(
                text = title,
                color = if (isSelected) Color.White else TextSecondary,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
fun JournalEntryItem(title: String, preview: String, date: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TextPrimary)
                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(MoodHappy)) // Yellow dot like in design
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = preview,
                color = TextSecondary,
                fontSize = 14.sp,
                maxLines = 2,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(text = date, color = TextHint, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}

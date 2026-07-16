package com.serenemind.ui.journal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    onAddClick: () -> Unit = {},
    onJournalClick: (Long) -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Journal", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                actions = {
                    IconButton(onClick = { /* Search */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = onAddClick) {
                        Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(28.dp))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = Color(0xFF673AB7),
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Journal")
            }
        },
        containerColor = Color(0xFFFBFBFF)
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Filter Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                JournalFilterTab("All", true)
                Spacer(modifier = Modifier.width(8.dp))
                JournalFilterTab("Favorites", false)
                Spacer(modifier = Modifier.width(8.dp))
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
                        preview = "Today was a good day. I finished my tasks and spent time with my family...",
                        date = "May 12, 2024",
                        onClick = { onJournalClick(1L) }
                    )
                }
                item {
                    JournalEntryItem(
                        title = "Grateful for little things",
                        preview = "I am grateful for my family, friends and good health...",
                        date = "May 10, 2024",
                        onClick = { onJournalClick(2L) }
                    )
                }
                item {
                    JournalEntryItem(
                        title = "Overthinking",
                        preview = "Sometimes I think too much about the future...",
                        date = "May 8, 2024",
                        onClick = { onJournalClick(3L) }
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
        color = if (isSelected) Color(0xFF673AB7) else Color(0xFFF5F5F5),
        modifier = Modifier.height(32.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = title,
                color = if (isSelected) Color.White else Color.Gray,
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
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF673AB7)))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = preview,
                color = Color.Gray,
                fontSize = 13.sp,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = date, color = Color.LightGray, fontSize = 11.sp)
        }
    }
}

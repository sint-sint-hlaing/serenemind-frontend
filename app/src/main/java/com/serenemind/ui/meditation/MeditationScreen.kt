package com.serenemind.ui.meditation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.serenemind.model.response.Meditation
import com.serenemind.model.response.MeditationCategory
import com.serenemind.model.response.MeditationDashboardResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeditationScreen(
    viewModel: MeditationViewModel,
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Meditation", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color(0xFFFBFBFF)
    ) { padding ->
        when (val state = uiState) {
            is MeditationUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is MeditationUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.message, color = Color.Red)
                }
            }
            is MeditationUiState.Success -> {
                MeditationContent(padding, state.data)
            }
            else -> {}
        }
    }
}

@Composable
fun MeditationContent(padding: PaddingValues, data: MeditationDashboardResponse) {
    Column(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        
        // Featured Card
        FeaturedMeditationCard(data.featured)

        Spacer(modifier = Modifier.height(28.dp))

        // Popular Categories Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Popular Categories", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            TextButton(
                onClick = { /* View all categories */ },
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("View all", color = Color(0xFF673AB7), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            items(data.categories) { category ->
                CategoryItem(category)
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Recommended Header
        Text("Recommended for you", fontWeight = FontWeight.Bold, fontSize = 17.sp)
        Spacer(modifier = Modifier.height(16.dp))

        data.recommended.forEach { meditation ->
            RecommendedItem(meditation)
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun FeaturedMeditationCard(meditation: Meditation) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box {
            // Gradient placeholder for landscapes
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF9575CD), Color(0xFF311B92))
                        )
                    )
            ) 
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = meditation.title,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${meditation.duration} • ${meditation.category}",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun CategoryItem(category: MeditationCategory) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(getCategoryBgColor(category.name))
                    .clickable { /* Select category */ },
                contentAlignment = Alignment.Center
            ) {
                Text(category.emoji, fontSize = 26.sp)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(category.name, fontSize = 13.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
    }
}

fun getCategoryBgColor(name: String): Color {
    return when(name.lowercase()) {
        "sleep" -> Color(0xFFF3E5F5)
        "anxiety" -> Color(0xFFE3F2FD)
        "focus" -> Color(0xFFE0F2F1)
        "morning" -> Color(0xFFFFF3E0)
        else -> Color(0xFFF5F5F5)
    }
}

@Composable
fun RecommendedItem(meditation: Meditation) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFE0E0E0))
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(meditation.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(meditation.duration, color = Color.Gray, fontSize = 14.sp)
        }
        IconButton(
            onClick = { /* Play */ },
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFFF3E5F5))
        ) {
            Icon(
                Icons.Default.PlayArrow,
                contentDescription = "Play",
                tint = Color(0xFF673AB7),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

package com.serenemind.ui.meditation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.serenemind.R
import com.serenemind.model.response.Meditation
import com.serenemind.model.response.MeditationCategory
import com.serenemind.model.response.MeditationDashboardResponse
import com.serenemind.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeditationScreen(
    viewModel: MeditationViewModel,
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        if (uiState is MeditationUiState.Idle) {
            viewModel.fetchMeditationDashboard()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Meditation", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (val state = uiState) {
                is MeditationUiState.Loading, MeditationUiState.Idle -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                is MeditationUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                            Text(text = "Error", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(text = state.message, color = Color.Gray, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.fetchMeditationDashboard() }) {
                                Text("Retry")
                            }
                        }
                    }
                }
                is MeditationUiState.Success -> {
                    MeditationContent(state.data)
                }
                else -> {}
            }
        }
    }
}

@Composable
fun MeditationContent(data: MeditationDashboardResponse) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Featured Card
        FeaturedMeditationCard(data.featured)

        Spacer(modifier = Modifier.height(32.dp))

        // Categories
        Text("Popular Categories", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(data.categories) { category ->
                CategoryItem(category)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Recommended
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Recommended for you", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
            Text("View all", color = MaterialTheme.colorScheme.primary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            data.recommended.forEach { meditation ->
                RecommendedItem(meditation)
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun FeaturedMeditationCard(meditation: Meditation) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = meditation.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.ic_launcher_background),
                placeholder = painterResource(R.drawable.ic_launcher_background)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                            startY = 100f
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = meditation.title,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                val durationDisplay = try {
                    val seconds = meditation.duration.toInt()
                    "${seconds / 60} min"
                } catch (e: Exception) {
                    meditation.duration
                }
                val categoryDisplay = meditation.category.lowercase().replaceFirstChar { it.uppercase() }
                Text(
                    text = "$durationDisplay • $categoryDisplay",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun CategoryItem(category: MeditationCategory) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(76.dp)) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier.border(1.dp, Color(0xFFF0F0F0), RoundedCornerShape(20.dp))
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(getCategoryBgColor(category.name))
                    .clickable { /* Select category */ },
                contentAlignment = Alignment.Center
            ) {
                val emoji = when(category.name.lowercase()) {
                    "sleep" -> "🌙"
                    "anxiety" -> "⚙️"
                    "focus" -> "🎯"
                    "morning" -> "☀️"
                    else -> category.emoji.ifEmpty { "🧘" }
                }
                Text(emoji, fontSize = 30.sp)
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = category.name.lowercase().replaceFirstChar { it.uppercase() }, 
            fontSize = 13.sp, 
            color = TextPrimary, 
            fontWeight = FontWeight.Bold
        )
    }
}

fun getCategoryBgColor(name: String): Color {
    return when(name.lowercase()) {
        "sleep" -> ActionJournal
        "anxiety" -> ActionMeditation
        "focus" -> ActionGoals
        "morning" -> ActionBreathing
        else -> Color(0xFFF5F5F5)
    }
}

@Composable
fun RecommendedItem(meditation: Meditation) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Start meditation */ },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = meditation.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(20.dp)),
            contentScale = ContentScale.Crop,
            error = painterResource(R.drawable.ic_launcher_background),
            placeholder = painterResource(R.drawable.ic_launcher_background)
        )
        Spacer(modifier = Modifier.width(20.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(meditation.title, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TextPrimary)
            val durationDisplay = try {
                val seconds = meditation.duration.toInt()
                "${seconds / 60} min"
            } catch (e: Exception) {
                meditation.duration
            }
            Text(durationDisplay, color = TextSecondary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
        IconButton(
            onClick = { /* Play */ },
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

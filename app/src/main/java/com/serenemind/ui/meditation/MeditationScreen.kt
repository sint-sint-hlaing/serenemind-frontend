package com.serenemind.ui.meditation

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.serenemind.R
import com.serenemind.model.response.MeditationCategory
import com.serenemind.model.response.MeditationDashboardResponse
import com.serenemind.model.response.MeditationResponse
import com.serenemind.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeditationScreen(
    viewModel: MeditationViewModel,
    onBack: () -> Unit = {},
    onMeditationClick: (MeditationResponse) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val continueListening by viewModel.continueListening.collectAsState()
    val recommendations by viewModel.recommendations.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(Unit) {
        if (uiState is MeditationUiState.Idle) {
            viewModel.fetchMeditationDashboard()
        }
        viewModel.fetchContinueListening()
        viewModel.fetchRecommendations()
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
                            Text(text = state.message, color = Color.Gray)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.fetchMeditationDashboard() }) {
                                Text("Retry")
                            }
                        }
                    }
                }
                is MeditationUiState.Success -> {
                    MeditationContent(
                        data = state.data,
                        continueListening = continueListening,
                        onMeditationClick = onMeditationClick,
                        onCategoryClick = { 
                            android.widget.Toast.makeText(context, "Category: $it", android.widget.Toast.LENGTH_SHORT).show()
                        },
                        onViewAllClick = {
                            android.widget.Toast.makeText(context, "Viewing all meditations", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MeditationContent(
    data: MeditationDashboardResponse,
    continueListening: List<MeditationResponse>,
    onMeditationClick: (MeditationResponse) -> Unit,
    onCategoryClick: (String) -> Unit = {},
    onViewAllClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Featured Card
        FeaturedMeditationCard(data.featured, onClick = { onMeditationClick(data.featured) })

        if (continueListening.isNotEmpty()) {
            Spacer(modifier = Modifier.height(32.dp))
            Text("Continue Listening", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
            Spacer(modifier = Modifier.height(16.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(continueListening) { meditation ->
                    ContinueListeningItem(meditation, onClick = { onMeditationClick(meditation) })
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Categories
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Popular Categories", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TextPrimary)
            Text(
                text = "View all",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onViewAllClick() }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(data.categories) { category ->
                CategoryItem(category, onClick = { onCategoryClick(category.name) })
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Recommended
        Text("Recommended for you", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TextPrimary)
        Spacer(modifier = Modifier.height(16.dp))
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            data.recommended.forEach { meditation ->
                RecommendedItem(meditation, onClick = { onMeditationClick(meditation) })
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ContinueListeningItem(meditation: MeditationResponse, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color(0xFFF0F0F0))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = meditation.imageUrl,
                contentDescription = null,
                modifier = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.ic_launcher_background)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(meditation.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1)
                Text("${meditation.duration} min left", fontSize = 12.sp, color = TextSecondary)
            }
        }
    }
}

@Composable
fun FeaturedMeditationCard(meditation: MeditationResponse, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = meditation.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.ic_launcher_background)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f)),
                            startY = 200f
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
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${meditation.duration} • ${meditation.category.lowercase().replaceFirstChar { it.uppercase() }}",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun CategoryItem(category: MeditationCategory, onClick: () -> Unit = {}) {
    val categoryInfo = when (category.name.lowercase()) {
        "sleep", "sleeps", "sleep_meditation" -> Triple(Color(0xFFF3E5F5), Color(0xFF7E57C2), Icons.Default.NightsStay)
        "anxiety", "anxious", "anxiety_relief" -> Triple(Color(0xFFE8EAF6), Color(0xFF5C6BC0), Icons.Default.Psychology)
        "focus", "focused" -> Triple(Color(0xFFE1F5FE), Color(0xFF29B6F6), Icons.Default.TrackChanges)
        "morning", "morning_breath", "relaxation" -> Triple(Color(0xFFFFF3E0), Color(0xFFFF9800), Icons.Default.WbSunny)
        else -> Triple(Color(0xFFF5F5F5), Color.Gray, Icons.Default.SelfImprovement)
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(76.dp)) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = categoryInfo.first,
            modifier = Modifier
                .size(64.dp)
                .clickable { onClick() }
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = categoryInfo.third,
                    contentDescription = null,
                    tint = categoryInfo.second,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = category.name.lowercase().replaceFirstChar { it.uppercase() },
            fontSize = 13.sp,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold
        )
    }
}

fun getCategoryBgColor(name: String): Color {
    return when(name.lowercase()) {
        "sleep" -> ActionJournal
        "anxiety" -> ActionMeditation
        "focus" -> ActionGoals
        else -> Color(0xFFF5F5F5)
    }
}

@Composable
fun RecommendedItem(meditation: MeditationResponse, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = meditation.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop,
            error = painterResource(R.drawable.ic_launcher_background)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(meditation.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
            Text(meditation.duration, color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

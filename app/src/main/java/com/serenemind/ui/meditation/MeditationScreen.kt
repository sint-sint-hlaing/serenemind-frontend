// MeditationScreen.kt
package com.serenemind.ui.meditation

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.serenemind.R
import com.serenemind.model.response.MeditationCategoryResponse
import com.serenemind.model.response.MeditationDashboardResponse
import com.serenemind.model.response.MeditationResponse
import com.serenemind.ui.theme.*

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeditationScreen(
    viewModel: MeditationViewModel,
    isDarkMode: Boolean,
    onBack: () -> Unit = {},
    onMeditationClick: (MeditationResponse) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val continueListening by viewModel.continueListening.collectAsState()
    val recommendations by viewModel.recommendations.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val context = LocalContext.current

    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (uiState is MeditationUiState.Idle) {
            viewModel.fetchMeditationDashboard()
        }
        viewModel.fetchContinueListening()
        viewModel.fetchRecommendations()
    }

    Scaffold(
        topBar = {
            if (isSearchActive) {
                TopAppBar(
                    title = {
                        TextField(
                            value = searchQuery,
                            onValueChange = {
                                searchQuery = it
                                if (it.length >= 2) {
                                    viewModel.search(it)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Search meditations...") },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                            ),
                            singleLine = true
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { 
                            isSearchActive = false
                            searchQuery = ""
                            viewModel.clearSearch()
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Close Search")
                        }
                    },
                    actions = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { 
                                searchQuery = ""
                                viewModel.clearSearch()
                            }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    }
                )
            } else {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Meditation",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { isSearchActive = true }) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search",
                                tint = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = if (isDarkMode) Color(0xFF1A1A1A) else MaterialTheme.colorScheme.surface
                    )
                )
            }
        },
        containerColor = if (isDarkMode) Color(0xFF1A1A1A) else MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (isSearchActive && (searchQuery.isNotEmpty() || searchResults.isNotEmpty())) {
                SearchContent(
                    results = searchResults,
                    isDarkMode = isDarkMode,
                    onMeditationClick = onMeditationClick
                )
            } else {
                when (val state = uiState) {
                    is MeditationUiState.Loading, MeditationUiState.Idle -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Loading meditations...",
                                    color = if (isDarkMode) Color.LightGray else TextSecondary
                                )
                            }
                        }
                    }
                    is MeditationUiState.Error -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(24.dp)
                            ) {
                                Text(
                                    text = "😌",
                                    fontSize = 48.sp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Something went wrong",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = if (isDarkMode) Color.White else TextPrimary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = state.message,
                                    color = if (isDarkMode) Color.LightGray else Color.Gray,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { viewModel.fetchMeditationDashboard() },
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Text("Retry")
                                }
                            }
                        }
                    }
                    is MeditationUiState.Success -> {
                        MeditationContent(
                            data = state.data,
                            continueListening = continueListening,
                            recommendations = if (searchResults.isNotEmpty()) searchResults else recommendations,
                            isDarkMode = isDarkMode,
                            onMeditationClick = onMeditationClick,
                            onCategoryClick = { categoryName ->
                                val categoryCode = state.data.categories?.find { it.displayName == categoryName }?.name ?: categoryName
                                viewModel.searchMeditations(null, categoryCode.uppercase(), null)
                            },
                            onTimeClick = { timeName ->
                                val timeCode = state.data.times?.find { it.displayName == timeName }?.name ?: timeName
                                viewModel.searchMeditations(null, null, timeCode.uppercase())
                            },
                            onViewAllClick = {
                                viewModel.searchMeditations(null, null, null)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MeditationContent(
    data: MeditationDashboardResponse,
    continueListening: List<MeditationResponse>,
    recommendations: List<MeditationResponse>,
    isDarkMode: Boolean,
    onMeditationClick: (MeditationResponse) -> Unit,
    onCategoryClick: (String) -> Unit = {},
    onTimeClick: (String) -> Unit = {},
    onViewAllClick: () -> Unit = {}
) {
    val textColor = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onBackground
    val textSecondary = if (isDarkMode) Color.LightGray else TextSecondary
    var selectedCategory by remember { mutableStateOf("All") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Categories Tabs
        val categories = listOf("All") + (data.categories?.mapNotNull { it.displayName } ?: emptyList())
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(categories) { category ->
                FilterTab(
                    title = category,
                    isSelected = selectedCategory == category,
                    isDarkMode = isDarkMode,
                    onClick = { 
                        selectedCategory = category
                        if (category != "All") onCategoryClick(category) 
                        else onViewAllClick()
                    }
                )
            }
        }

        // Recommended
        Text(
            "Recommended for you",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = textColor
        )
        Spacer(modifier = Modifier.height(16.dp))

        val displayList = if (recommendations.isNotEmpty()) recommendations else data.popular ?: emptyList()
        
        if (displayList.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                displayList.forEach { meditation ->
                    RecommendedItem(
                        meditation = meditation,
                        isDarkMode = isDarkMode,
                        onClick = { onMeditationClick(meditation) }
                    )
                }
            }
        } else {
            Text(
                "No recommendations available",
                color = textSecondary,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun SearchContent(
    results: List<MeditationResponse>,
    isDarkMode: Boolean,
    onMeditationClick: (MeditationResponse) -> Unit
) {
    if (results.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                "No results found",
                color = if (isDarkMode) Color.LightGray else TextSecondary
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            results.forEach { meditation ->
                RecommendedItem(
                    meditation = meditation,
                    isDarkMode = isDarkMode,
                    onClick = { onMeditationClick(meditation) }
                )
            }
        }
    }
}

@Composable
fun ContinueListeningItem(
    meditation: MeditationResponse,
    isDarkMode: Boolean,
    onClick: () -> Unit
) {
    val surfaceColor = if (isDarkMode) Color(0xFF2A2A2A) else Color.White
    val textColor = if (isDarkMode) Color.White else TextPrimary
    val textSecondary = if (isDarkMode) Color.LightGray else TextSecondary

    Card(
        modifier = Modifier
            .width(220.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = surfaceColor
        ),
        border = BorderStroke(
            1.dp,
            if (isDarkMode) Color(0xFF333333) else Color(0xFFF0F0F0)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = meditation.getCleanImageUrl(),
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.ic_launcher_background)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    meditation.title ?: "Untitled",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    color = textColor
                )
                Text(
                    "${meditation.duration ?: "5"} min left",
                    fontSize = 12.sp,
                    color = textSecondary
                )
            }
        }
    }
}

@Composable
fun FeaturedMeditationCard(
    meditation: MeditationResponse,
    onClick: () -> Unit
) {
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
                model = meditation.getCleanImageUrl(),
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
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
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
                    text = meditation.title ?: "Untitled",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${meditation.duration ?: "5"} • ${meditation.category?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "Meditation"}",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun CategoryItem(
    category: MeditationCategoryResponse,
    isDarkMode: Boolean,
    onClick: () -> Unit = {}
) {
    val categoryName = category.name?.lowercase() ?: ""
    val categoryInfo = when {
        categoryName.contains("sleep") ->
            Triple(Color(0xFFF3E5F5), Color(0xFF7E57C2), Icons.Default.NightsStay)
        categoryName.contains("anxiety") ->
            Triple(Color(0xFFE8EAF6), Color(0xFF5C6BC0), Icons.Default.Psychology)
        categoryName.contains("focus") ->
            Triple(Color(0xFFE1F5FE), Color(0xFF29B6F6), Icons.Default.TrackChanges)
        categoryName.contains("morning") || categoryName.contains("relaxation") ->
            Triple(Color(0xFFFFF3E0), Color(0xFFFF9800), Icons.Default.WbSunny)
        categoryName.contains("breathing") ->
            Triple(Color(0xFFE0F7FA), Color(0xFF00ACC1), Icons.Default.Air)
        categoryName.contains("stress") ->
            Triple(Color(0xFFFFEBEE), Color(0xFFEF5350), Icons.Default.HealthAndSafety)
        else -> Triple(Color(0xFFF5F5F5), Color.Gray, Icons.Default.SelfImprovement)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(76.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (isDarkMode) categoryInfo.first.copy(alpha = 0.2f) else categoryInfo.first,
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
            text = category.displayName ?: categoryName.replaceFirstChar { it.uppercase() },
            fontSize = 13.sp,
            color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

@Composable
fun TimeItem(
    time: com.serenemind.model.response.MeditationTimeResponse,
    isDarkMode: Boolean,
    onClick: () -> Unit = {}
) {
    val timeName = time.name?.lowercase() ?: ""
    val surfaceColor = if (isDarkMode) Color(0xFF2A2A2A) else Color.White
    val textColor = if (isDarkMode) Color.White else TextPrimary

    Card(
        modifier = Modifier
            .width(140.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = surfaceColor
        ),
        border = BorderStroke(
            1.dp,
            if (isDarkMode) Color(0xFF333333) else Color(0xFFF0F0F0)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = time.emoji ?: "✨",
                fontSize = 32.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = time.displayName ?: timeName.replaceFirstChar { it.uppercase() },
                fontSize = 14.sp,
                color = textColor,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}

@Composable
fun FilterTab(
    title: String,
    isSelected: Boolean,
    isDarkMode: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(100.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary else if (isDarkMode) Color(0xFF2A2A2A) else Color(0xFFF5F5F5),
        modifier = Modifier.height(38.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            Text(
                text = title,
                color = if (isSelected) Color.White else if (isDarkMode) Color.LightGray else TextSecondary,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
fun RecommendedItem(
    meditation: MeditationResponse,
    isDarkMode: Boolean,
    onClick: () -> Unit
) {
    val textColor = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onSurface
    val textSecondary = if (isDarkMode) Color.LightGray else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = meditation.getCleanImageUrl(),
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop,
            error = painterResource(R.drawable.ic_launcher_background)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                meditation.title ?: "Untitled",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = textColor,
                maxLines = 1
            )
            Text(
                "${meditation.duration ?: "5"} min",
                color = textSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
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

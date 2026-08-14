// MeditationPlayerScreen.kt
package com.serenemind.ui.meditation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
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
import com.serenemind.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeditationPlayerScreen(
    viewModel: MeditationViewModel,
    isDarkMode: Boolean,
    onBack: () -> Unit = {},
    onNavigateToTimer: () -> Unit = {}
) {
    val meditation by viewModel.selectedMeditation.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // ⚠️ FIXED: ViewModel မှာ isLoading မရှိလို့ state ကို ဖယ်ရှားခဲ့ပါတယ်
    // loading state အတွက် uiState ကိုသုံးပါမယ်
    val uiState by viewModel.uiState.collectAsState()

    var isPlaying by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0.35f) }
    var currentTime by remember { mutableStateOf("02:35") }
    var totalTime by remember { mutableStateOf("04:00") }
    var isFavorite by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Update favorite when meditation changes
    LaunchedEffect(meditation) {
        isFavorite = meditation?.favorite ?: false
        totalTime = "${meditation?.duration ?: "4"}:00"
    }

    // Show error messages
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            // Clear error after showing
            viewModel.clearError()
        }
    }

    // Get colors based on dark mode
    val backgroundColor = if (isDarkMode) Color(0xFF1A1A1A) else Color.White
    val surfaceColor = if (isDarkMode) Color(0xFF2A2A2A) else Color(0xFFF5F5F5)
    val textColor = if (isDarkMode) Color.White else TextPrimary
    val textSecondaryColor = if (isDarkMode) Color.LightGray else TextSecondary

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Meditation",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = textColor
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = textColor
                        )
                    }
                },
                actions = {
                    var showMenu by remember { mutableStateOf(false) }
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "More",
                            tint = textColor
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        containerColor = backgroundColor
                    ) {
                        DropdownMenuItem(
                            text = { Text("Set Timer", color = textColor) },
                            onClick = {
                                showMenu = false
                                onNavigateToTimer()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Download Audio", color = textColor) },
                            onClick = {
                                showMenu = false
                                meditation?.let {
                                    viewModel.downloadAudio(it.id) { msg ->
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Share", color = textColor) },
                            onClick = {
                                showMenu = false
                                meditation?.let {
                                    viewModel.getShareLink(it.id) { url ->
                                        val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(android.content.Intent.EXTRA_TEXT, "Check out this meditation: $url")
                                        }
                                        context.startActivity(android.content.Intent.createChooser(intent, "Share Meditation"))
                                    }
                                }
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = backgroundColor
                )
            )
        },
        containerColor = backgroundColor
    ) { padding ->
        // ⚠️ FIXED: isLoading ကို uiState နဲ့ အစားထိုးခဲ့ပါတယ်
        if (uiState is MeditationUiState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Loading...",
                        color = textSecondaryColor
                    )
                }
            }
        } else {
            meditation?.let { m ->
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Meditation Image/Video Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = surfaceColor
                        )
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            // Audio/Video Player integration
                            if (isPlaying && m.getCleanAudioUrl()?.isNotEmpty() == true) {
                                VideoPlayer(
                                    videoUrl = m.getCleanAudioUrl()!!,
                                    isPlaying = isPlaying,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                AsyncImage(
                                    model = m.getCleanImageUrl(),
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
                                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                                                startY = 300f
                                            )
                                        )
                                )
                            }

                            if (!isPlaying) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(24.dp),
                                    verticalArrangement = Arrangement.Bottom
                                ) {
                                    Text(
                                        m.title ?: "Untitled",
                                        color = Color.White,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "${m.duration ?: "4"} min • Guided Meditation",
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            // Play/Pause overlay button
                            if (!isPlaying) {
                                Surface(
                                    onClick = { isPlaying = true },
                                    shape = CircleShape,
                                    color = Color.White.copy(alpha = 0.9f),
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .size(56.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Filled.PlayArrow,
                                            contentDescription = "Play",
                                            modifier = Modifier.size(32.dp),
                                            // ⚠️ FIXED: PrimaryLight ကို Primary နဲ့ အစားထိုးခဲ့ပါတယ်
                                            tint = PrimaryLight
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Title and description
                    Text(
                        m.title ?: "Untitled",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Text(
                        "${m.duration ?: "4"} min • ${m.category ?: "Meditation"}",
                        fontSize = 14.sp,
                        color = textSecondaryColor
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Progress Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(currentTime, fontSize = 12.sp, color = textSecondaryColor)
                        Text(totalTime, fontSize = 12.sp, color = textSecondaryColor)
                    }
                    Slider(
                        value = progress,
                        onValueChange = { progress = it },
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = if (isDarkMode) Color(0xFF333333) else Color(0xFFF5F5F5)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Previous
                        IconButton(
                            onClick = {
                                viewModel.navigateToPrevious()
                            },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                Icons.Default.SkipPrevious,
                                contentDescription = "Previous",
                                modifier = Modifier.size(28.dp),
                                tint = textColor
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Rewind
                        IconButton(
                            onClick = {
                                progress = (progress - 0.05f).coerceAtLeast(0f)
                                Toast.makeText(context, "Rewinding 15s", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                Icons.Outlined.Replay,
                                contentDescription = "Rewind",
                                modifier = Modifier.size(28.dp),
                                tint = textColor
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Play/Pause Main
                        // ✅ Alternative - Using Surface without elevation
                        Surface(
                            onClick = { isPlaying = !isPlaying },
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            shadowElevation = 8.dp,  // ✅ Use shadowElevation instead of elevation
                            modifier = Modifier.size(72.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                    contentDescription = if (isPlaying) "Pause" else "Play",
                                    modifier = Modifier.size(36.dp),
                                    tint = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Fast Forward
                        IconButton(
                            onClick = {
                                progress = (progress + 0.05f).coerceAtMost(1f)
                                Toast.makeText(context, "Fast Forward 15s", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                Icons.Outlined.Forward,
                                contentDescription = "Fast Forward",
                                modifier = Modifier.size(28.dp),
                                tint = textColor
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Next
                        IconButton(
                            onClick = {
                                viewModel.navigateToNext()
                            },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                Icons.Default.SkipNext,
                                contentDescription = "Next",
                                modifier = Modifier.size(28.dp),
                                tint = textColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // Bottom Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        PlayerAction(
                            icon = Icons.Outlined.FileDownload,
                            label = "Download",
                            onClick = {
                                viewModel.downloadAudio(m.id) { msg ->
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                            }
                        )

                        PlayerAction(
                            icon = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.Favorite,
                            label = "Favorite",
                            color = if (isFavorite) Color.Red else textColor,
                            onClick = {
                                viewModel.toggleFavorite(m.id)
                                // ⚠️ FIXED: isFavorite ကို update လုပ်ပါတယ်
                                isFavorite = !isFavorite
                                val msg = if (isFavorite) "Added to favorites" else "Removed from favorites"
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        )

                        PlayerAction(
                            icon = Icons.Outlined.Timer,
                            label = "Timer",
                            onClick = onNavigateToTimer
                        )

                        PlayerAction(
                            icon = Icons.Outlined.Share,
                            label = "Share",
                            onClick = {
                                viewModel.getShareLink(m.id) { url ->
                                    val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(android.content.Intent.EXTRA_TEXT, "Check out this meditation: $url")
                                    }
                                    context.startActivity(android.content.Intent.createChooser(intent, "Share Meditation"))
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // About Section
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "About this session",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = textColor
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            m.description ?: "No description available for this meditation.",
                            color = textSecondaryColor,
                            fontSize = 15.sp,
                            lineHeight = 22.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            } ?: run {
                // No meditation selected
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🧘", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No meditation selected",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = textColor
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Please select a meditation to start",
                            fontSize = 14.sp,
                            color = textSecondaryColor
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onBack,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Go Back")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Icon(
            icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = color
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
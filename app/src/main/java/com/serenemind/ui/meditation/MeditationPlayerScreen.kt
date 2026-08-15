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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
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
    val isPlaying by viewModel.isPlaying.collectAsState()
    val timerSeconds by viewModel.timerSeconds.collectAsState()
    val isTimerRunning by viewModel.isTimerRunning.collectAsState()
    val isTimerCompleted by viewModel.isTimerCompleted.collectAsState()
    val downloadState by viewModel.downloadState.collectAsState()

    var progress by remember { mutableFloatStateOf(0.0f) }
    var currentTime by remember { mutableStateOf("00:00") }
    var totalTime by remember { mutableStateOf("00:00") }
    var isFavorite by remember { mutableStateOf(false) }
    var isDragging by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // ===== EXOPLAYER INITIALIZATION =====
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            addListener(object : Player.Listener {
                override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                    android.util.Log.e("MeditationPlayer", "ExoPlayer Error: ${error.errorCodeName} - ${error.message}", error)
                    Toast.makeText(context, "Playback error: ${error.localizedMessage}", Toast.LENGTH_LONG).show()
                }

                override fun onPlaybackStateChanged(state: Int) {
                    when (state) {
                        Player.STATE_BUFFERING -> android.util.Log.d("MeditationPlayer", "Buffering...")
                        Player.STATE_READY -> {
                            android.util.Log.d("MeditationPlayer", "Ready to play. Duration: ${duration}")
                            if (duration > 0) {
                                val totalMins = (duration / 1000) / 60
                                val totalSecs = (duration / 1000) % 60
                                totalTime = String.format(java.util.Locale.getDefault(), "%02d:%02d", totalMins, totalSecs)
                            }
                        }
                        Player.STATE_ENDED -> {
                            android.util.Log.d("MeditationPlayer", "Playback Ended")
                            viewModel.resetTimer()
                        }
                        Player.STATE_IDLE -> android.util.Log.d("MeditationPlayer", "Player Idle")
                    }
                }
            })
        }
    }

    // Release player when Composable is disposed
    DisposableEffect(Unit) {
        onDispose {
            android.util.Log.d("MeditationPlayer", "Releasing ExoPlayer")
            exoPlayer.release()
        }
    }

    // Update media item when meditation changes
    LaunchedEffect(meditation) {
        meditation?.getCleanAudioUrl()?.let { url ->
            android.util.Log.d("MeditationPlayer", "Loading URL: $url")
            if (url.isNotBlank()) {
                val mediaItem = MediaItem.fromUri(url)
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
            }
        }
        isFavorite = meditation?.favorite ?: false
    }

    // Show error messages
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    // Sync playback state
    LaunchedEffect(isPlaying) {
        android.util.Log.d("MeditationPlayer", "Syncing isPlaying: $isPlaying")
        exoPlayer.playWhenReady = isPlaying
        if (isPlaying && exoPlayer.playbackState == Player.STATE_IDLE) {
            exoPlayer.prepare()
        }
    }

    // Update progress and current time
    LaunchedEffect(isPlaying, isDragging) {
        while (isPlaying && !isDragging) {
            val currentPos = exoPlayer.currentPosition
            val duration = exoPlayer.duration
            if (duration > 0) {
                progress = (currentPos.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
                
                val mins = (currentPos / 1000) / 60
                val secs = (currentPos / 1000) % 60
                currentTime = String.format(java.util.Locale.getDefault(), "%02d:%02d", mins, secs)
            }
            delay(500)
        }
    }

    // Handle download state messages
    LaunchedEffect(downloadState) {
        val currentDownloadState = downloadState
        when (currentDownloadState) {
            is DownloadUiState.Success -> {
                Toast.makeText(context, "Download started", Toast.LENGTH_SHORT).show()
                viewModel.resetDownloadState()
            }
            is DownloadUiState.Error -> {
                Toast.makeText(context, currentDownloadState.message, Toast.LENGTH_LONG).show()
                viewModel.resetDownloadState()
            }
            else -> {}
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
                                    viewModel.startDownload(context, it.id, it.title)
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
                    // Meditation Image Card with Text Overlay
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            // Background Image
                            AsyncImage(
                                model = m.getCleanImageUrl(),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                error = painterResource(R.drawable.ic_launcher_background)
                            )
                            
                            // Dark Gradient Overlay
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                                            startY = 300f
                                        )
                                    )
                            )

                            // Title & Duration Text (Bottom Left)
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(24.dp)
                            ) {
                                Text(
                                    text = m.title ?: "Untitled",
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${m.duration ?: "10"} min - Guided Meditation",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 14.sp
                                )
                            }

                            // Timer overlay if running
                            if (isTimerRunning || isTimerCompleted) {
                                val minutes = timerSeconds / 60
                                val seconds = timerSeconds % 60
                                val timeStr = if (isTimerCompleted) "Done!" else String.format(java.util.Locale.getDefault(), "%02d:%02d", minutes, seconds)
                                
                                Surface(
                                    color = Color.Black.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(16.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = timeStr,
                                            color = if (isTimerCompleted) Success else Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        )
                                        if (isTimerRunning) {
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Icon(
                                                Icons.Default.Pause,
                                                contentDescription = "Pause Timer",
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp).clickable { viewModel.pauseTimer() }
                                            )
                                        } else if (timerSeconds > 0 && !isTimerCompleted) {
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Icon(
                                                Icons.Default.PlayArrow,
                                                contentDescription = "Resume Timer",
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp).clickable { viewModel.resumeTimer() }
                                            )
                                        }
                                        if (timerSeconds > 0 || isTimerCompleted) {
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Icon(
                                                Icons.Default.Refresh,
                                                contentDescription = "Reset",
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp).clickable { viewModel.resetTimer() }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Progress Slider & Time
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currentTime,
                            fontSize = 12.sp,
                            color = textSecondaryColor,
                            fontWeight = FontWeight.Medium
                        )
                        Slider(
                            value = progress,
                            onValueChange = { 
                                isDragging = true
                                progress = it
                                if (exoPlayer.duration > 0) {
                                    val seekPos = (it * exoPlayer.duration).toLong()
                                    val mins = (seekPos / 1000) / 60
                                    val secs = (seekPos / 1000) % 60
                                    currentTime = String.format(java.util.Locale.getDefault(), "%02d:%02d", mins, secs)
                                }
                            },
                            onValueChangeFinished = {
                                isDragging = false
                                if (exoPlayer.duration > 0) {
                                    exoPlayer.seekTo((progress * exoPlayer.duration).toLong())
                                }
                            },
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary,
                                inactiveTrackColor = if (isDarkMode) Color(0xFF333333) else Color(0xFFF5F5F5)
                            ),
                            modifier = Modifier.weight(1f).padding(horizontal = 12.dp)
                        )
                        Text(
                            text = totalTime,
                            fontSize = 12.sp,
                            color = textSecondaryColor,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Playback Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Rewind 15s
                        IconButton(
                            onClick = { 
                                val newPos = (exoPlayer.currentPosition - 15000).coerceAtLeast(0)
                                exoPlayer.seekTo(newPos)
                            },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Replay10, // Close enough to 15
                                contentDescription = "Rewind",
                                modifier = Modifier.size(32.dp),
                                tint = textSecondaryColor
                            )
                        }

                        Spacer(modifier = Modifier.width(24.dp))

                        // Play/Pause Main
                        Surface(
                            onClick = { viewModel.togglePlayback() },
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(80.dp),
                            shadowElevation = 4.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                    contentDescription = if (isPlaying) "Pause" else "Play",
                                    modifier = Modifier.size(40.dp),
                                    tint = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(24.dp))

                        // Forward 15s
                        IconButton(
                            onClick = { 
                                val newPos = (exoPlayer.currentPosition + 15000).coerceAtMost(exoPlayer.duration)
                                exoPlayer.seekTo(newPos)
                            },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Forward10, // Close enough to 15
                                contentDescription = "Forward",
                                modifier = Modifier.size(32.dp),
                                tint = textSecondaryColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(48.dp))

                    // Action Icons (Download, Favorite, Timer, Share)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        PlayerActionItem(
                            icon = if (downloadState is DownloadUiState.Downloading) Icons.Default.Downloading else Icons.Outlined.FileDownload,
                            label = if (downloadState is DownloadUiState.Downloading) "Downloading" else "Download",
                            enabled = downloadState !is DownloadUiState.Downloading,
                            onClick = { 
                                meditation?.let { 
                                    viewModel.startDownload(context, it.id, it.title) 
                                } 
                            }
                        )
                        PlayerActionItem(
                            icon = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.Favorite,
                            label = "Favorite",
                            color = if (isFavorite) Color.Red else textSecondaryColor,
                            onClick = {
                                viewModel.toggleFavorite(m.id)
                                isFavorite = !isFavorite
                            }
                        )
                        PlayerActionItem(
                            icon = Icons.Outlined.Timer,
                            label = "Timer",
                            onClick = onNavigateToTimer
                        )
                        PlayerActionItem(
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

                    // Divider
                    HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = surfaceColor)

                    Spacer(modifier = Modifier.height(32.dp))

                    // About Section
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "About this session",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = textColor
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = m.description ?: "A gentle meditation to help you relax your mind and release stress.",
                            color = textSecondaryColor,
                            fontSize = 15.sp,
                            lineHeight = 22.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
?: run {
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
fun PlayerActionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color = MaterialTheme.colorScheme.onSurface,
    enabled: Boolean = true,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(80.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(8.dp)
            .alpha(if (enabled) 1f else 0.5f)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(28.dp),
            tint = color
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
    }
}



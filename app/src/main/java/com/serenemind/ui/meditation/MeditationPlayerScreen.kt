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
import com.serenemind.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeditationPlayerScreen(
    viewModel: MeditationViewModel,
    isDarkMode: Boolean,
    onBack: () -> Unit = {},
    onNavigateToTimer: () -> Unit = {}
) {
    val meditation by viewModel.selectedMeditation.collectAsState()
    var isPlaying by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0.35f) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Meditation", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
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
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (isPlaying && m.audioUrl.isNotEmpty()) {
                            VideoPlayer(videoUrl = m.audioUrl, isPlaying = isPlaying)
                        } else {
                            AsyncImage(
                                model = m.imageUrl,
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
                                modifier = Modifier.fillMaxSize().padding(24.dp),
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                Text(m.title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                                Text("${m.duration} min • Guided Meditation", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Progress Slider
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("02:35", fontSize = 12.sp, color = TextSecondary)
                    Text("${m.duration}:00", fontSize = 12.sp, color = TextSecondary)
                }
                Slider(
                    value = progress,
                    onValueChange = { progress = it },
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = if (isDarkMode) Color(0xFF333333) else Color(0xFFF5F5F5)
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { 
                        viewModel.navigateToPrevious()
                        Toast.makeText(context, "Previous session", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.SkipPrevious, contentDescription = "Previous", modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    IconButton(onClick = { 
                        Toast.makeText(context, "Rewinding 15s", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Rewind", modifier = Modifier.size(28.dp))
                    }
                    Spacer(modifier = Modifier.width(24.dp))
                    Surface(
                        onClick = { isPlaying = !isPlaying },
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(72.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                modifier = Modifier.size(36.dp),
                                tint = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(24.dp))
                    IconButton(onClick = { 
                        // Simulate progress for UI responsiveness
                        progress = (progress + 0.1f).coerceAtMost(1f)
                        Toast.makeText(context, "Fast Forward 15s", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.FastForward, contentDescription = "Fast Forward", modifier = Modifier.size(28.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    IconButton(onClick = { 
                        viewModel.navigateToNext()
                        Toast.makeText(context, "Next session", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.SkipNext, contentDescription = "Next", modifier = Modifier.size(32.dp))
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Bottom Actions
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    PlayerAction(
                        icon = Icons.Default.FileDownload, 
                        label = "Download",
                        onClick = { 
                            viewModel.downloadAudio(m.id) { msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                    PlayerAction(
                        icon = if (m.favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, 
                        label = "Favorite",
                        color = if (m.favorite) Color.Red else TextPrimary,
                        onClick = { 
                            viewModel.toggleFavorite(m.id)
                            val msg = if (!m.favorite) "Added to favorites" else "Removed from favorites"
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    )
                    PlayerAction(
                        icon = Icons.Default.Timer, 
                        label = "Timer",
                        onClick = onNavigateToTimer
                    )
                    PlayerAction(
                        icon = Icons.Default.Share, 
                        label = "Share",
                        onClick = { 
                            viewModel.getShareLink(m.id) { url ->
                                // Trigger Android Share Intent
                                val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(android.content.Intent.EXTRA_TEXT, "Check out this meditation: $url")
                                }
                                context.startActivity(android.content.Intent.createChooser(intent, "Share Meditation"))
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                // About Section
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("About this session", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        m.description,
                        color = TextSecondary,
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )
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
        Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp), tint = color)
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

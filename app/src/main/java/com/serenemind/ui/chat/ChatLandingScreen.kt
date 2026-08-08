package com.serenemind.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.serenemind.model.response.ConversationResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatLandingScreen(
    viewModel: ChatLandingViewModel,
    isDarkMode: Boolean,
    onStartChat: (String?) -> Unit,
    onViewConversation: (Long) -> Unit,
    onViewAll: () -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("SereneMind AI ", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            Text("✦", color = MaterialTheme.colorScheme.primary, fontSize = 18.sp)
                        }
                        Text("Your mindful AI companion", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Header Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        if (isDarkMode) {
                            Brush.verticalGradient(listOf(Color(0xFF2A2A4E), Color(0xFF1A1A2E)))
                        } else {
                            Brush.verticalGradient(listOf(Color(0xFFEDE7F6), Color(0xFFF3E5F5)))
                        }
                    )
                    .padding(24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = CircleShape,
                        color = if (isDarkMode) Color.White.copy(alpha = 0.1f) else Color.White
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = if (isDarkMode) Color.White else Color(0xFF7E57C2)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Column {
                        Text(
                            text = "Hi, I'm SereneAI 💜",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = if (isDarkMode) Color.White else Color(0xFF4527A0)
                        )
                        Text(
                            text = "I'm here to listen, support, and help you feel better.",
                            fontSize = 13.sp,
                            color = if (isDarkMode) Color.White.copy(alpha = 0.7f) else Color(0xFF4527A0).copy(alpha = 0.7f),
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Feature Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FeaturePill(Icons.Outlined.FavoriteBorder, "Non-judgmental", isDarkMode)
                FeaturePill(Icons.Outlined.AccessTime, "Always here", isDarkMode)
                FeaturePill(Icons.Outlined.Shield, "100% Private", isDarkMode)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Start a conversation", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(16.dp))

            // Prompts
            val prompts = listOf(
                PromptData("😫", "I'm feeling stressed", "Talk about what's on your mind"),
                PromptData("🌙", "I can't sleep well", "Let's try to understand why"),
                PromptData("⭐", "I need motivation", "Help me get inspired"),
                PromptData("😢", "I feel anxious", "Ways to manage anxiety"),
                PromptData("💬", "Just want to talk", "I'm here to listen to you")
            )

            prompts.forEach { prompt ->
                PromptCard(prompt, isDarkMode) { onStartChat(prompt.title) }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Previous conversations", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                TextButton(onClick = onViewAll) {
                    Text("View all", color = Color(0xFF7E57C2), fontSize = 12.sp)
                }
            }

            when (val state = uiState) {
                is ChatLandingUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                is ChatLandingUiState.Error -> Text(state.message, color = Color.Red)
                is ChatLandingUiState.Success -> {
                    state.conversations.take(3).forEach { item ->
                        HistoryItem(item, isDarkMode) { onViewConversation(item.id) }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun FeaturePill(icon: ImageVector, label: String, isDarkMode: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

data class PromptData(val emoji: String, val title: String, val subtitle: String)

@Composable
fun PromptCard(data: PromptData, isDarkMode: Boolean = false, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        border = if (isDarkMode) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF333333)) else null
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(data.emoji, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(data.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(data.subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos, 
                contentDescription = null, 
                modifier = Modifier.size(14.dp), 
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun HistoryItem(item: ConversationResponse, isDarkMode: Boolean = false, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        border = if (isDarkMode) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF333333)) else null
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(item.title ?: "New Chat", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text(item.createdAt?.take(10) ?: "", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                }
                val lastMsg = item.messages.lastOrNull()?.content ?: "No messages"
                Text(lastMsg, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, maxLines = 1)
            }
        }
    }
}

package com.serenemind.ui.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.serenemind.model.response.NotificationResponse
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    viewModel: NotificationViewModel,
    onNavigateToPost: (Long) -> Unit,
    onNavigateToReminder: (Long) -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("All", "Unread", "Mentions", "System")

    LaunchedEffect(Unit) {
        viewModel.fetchNotifications(tabs[selectedTabIndex].lowercase())
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is NotificationNavigationEvent.NavigateToPost -> onNavigateToPost(event.postId)
                is NotificationNavigationEvent.NavigateToComment -> onNavigateToPost(event.postId)
                is NotificationNavigationEvent.NavigateToReminder -> onNavigateToReminder(event.reminderId)
                is NotificationNavigationEvent.ShowSystemDialog -> { /* Show Dialog */ }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.markAllAsRead() }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 16.dp,
                divider = {},
                indicator = {},
                containerColor = Color.Transparent,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { 
                            selectedTabIndex = index
                            viewModel.fetchNotifications(title.lowercase())
                        },
                        text = {
                            Surface(
                                color = if (selectedTabIndex == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text(
                                    text = title,
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                                    color = if (selectedTabIndex == index) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    )
                }
            }

            when (val state = uiState) {
                is NotificationUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                is NotificationUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
                is NotificationUiState.Success -> {
                    NotificationList(
                        notifications = state.notifications,
                        onNotificationClick = { notification ->
                            viewModel.onNotificationClicked(notification.id)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationList(
    notifications: List<NotificationResponse>,
    onNotificationClick: (NotificationResponse) -> Unit,
    modifier: Modifier = Modifier
) {
    val groupedNotifications = remember(notifications) {
        groupNotifications(notifications)
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        groupedNotifications.forEach { (header, items) ->
            item {
                Text(
                    text = header,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            items(items) { notification ->
                NotificationItem(
                    notification = notification,
                    onClick = { onNotificationClick(notification) }
                )
            }
        }
    }
}

@Composable
fun NotificationItem(
    notification: NotificationResponse,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        val (icon, bgColor, iconColor) = getNotificationTypeDesign(notification.type)
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Content
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = notification.title ?: "",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = notification.message ?: "",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
            Text(
                text = formatTimeAgo(notification.createdAt),
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Read Indicator
        if (!notification.isRead) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
            )
        }
    }
}

data class NotificationDesign(val icon: ImageVector, val bgColor: Color, val iconColor: Color)

@Composable
fun getNotificationTypeDesign(type: String?): NotificationDesign {
    val safeType = type?.uppercase() ?: "DEFAULT"
    return when (safeType) {
        "LIKE" -> NotificationDesign(
            Icons.Default.ThumbUp, 
            Color(0xFFEDE7F6), 
            Color(0xFF673AB7)
        )
        "COMMENT" -> NotificationDesign(
            Icons.Default.Favorite, 
            Color(0xFFFCE4EC), 
            Color(0xFFE91E63)
        )
        "REMINDER" -> NotificationDesign(
            Icons.Default.NotificationsNone, 
            Color(0xFFE8F5E9), 
            Color(0xFF4CAF50)
        )
        "SYSTEM" -> NotificationDesign(
            Icons.Default.FileUpload, 
            Color(0xFFE3F2FD), 
            Color(0xFF2196F3)
        )
        "GOAL" -> NotificationDesign(
            Icons.Default.Star, 
            Color(0xFFFFF8E1), 
            Color(0xFFFFC107)
        )
        "STREAK" -> NotificationDesign(
            Icons.Default.Whatshot, 
            Color(0xFFFBE9E7), 
            Color(0xFFFF5722)
        )
        "QUOTE" -> NotificationDesign(
            Icons.Default.FormatQuote, 
            Color(0xFFF3E5F5), 
            Color(0xFF9C27B0)
        )
        else -> NotificationDesign(
            Icons.Default.Notifications, 
            MaterialTheme.colorScheme.surfaceVariant, 
            MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

fun groupNotifications(notifications: List<NotificationResponse>): Map<String, List<NotificationResponse>> {
    val dateFormats = listOf(
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault()),
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    )
    val today = Calendar.getInstance()
    val yesterday = Calendar.getInstance().apply { add(Calendar.DATE, -1) }
    
    return notifications.groupBy { notification ->
        var notificationDate: Date? = null
        val createdAt = notification.createdAt
        if (createdAt != null) {
            for (format in dateFormats) {
                try {
                    notificationDate = format.parse(createdAt)
                    if (notificationDate != null) break
                } catch (e: Exception) { }
            }
        }
        
        val date = notificationDate ?: Date()
        val cal = Calendar.getInstance().apply { time = date }
        
        when {
            isSameDay(cal, today) -> "Today"
            isSameDay(cal, yesterday) -> "Yesterday"
            else -> "Earlier"
        }
    }
}

fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
           cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

fun formatTimeAgo(dateStr: String?): String {
    if (dateStr == null) return ""
    val dateFormats = listOf(
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault()),
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    )
    
    var parsedDate: Date? = null
    for (format in dateFormats) {
        try {
            parsedDate = format.parse(dateStr)
            if (parsedDate != null) break
        } catch (e: Exception) { }
    }
    
    val date = parsedDate ?: return dateStr
    val diff = System.currentTimeMillis() - date.time
    
    return when {
        diff < 60 * 1000 -> "Just now"
        diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)}m ago"
        diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)}h ago"
        else -> {
            val outputSdf = SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault())
            outputSdf.format(date)
        }
    }
}

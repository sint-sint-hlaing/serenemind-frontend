package com.serenemind.ui.community

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import androidx.compose.ui.tooling.preview.Preview
import com.serenemind.R
import com.serenemind.model.response.PostResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    viewModel: CommunityViewModel,
    onPostClick: (PostResponse, Boolean) -> Unit,
    onCreatePostClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Popular", "Recent", "Following")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Community", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreatePostClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Post")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 16.dp,
                divider = {},
                indicator = {},
                containerColor = Color.Transparent
            ) {
                tabs.forEachIndexed { index, title ->
                    val isSelected = selectedTab == index
                    Tab(
                        selected = isSelected,
                        onClick = { selectedTab = index },
                        selectedContentColor = Color.Transparent,
                        unselectedContentColor = Color.Transparent,
                        interactionSource = remember { MutableInteractionSource() },
                        text = {
                            Surface(
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier
                                    .padding(vertical = 4.dp, horizontal = 4.dp),
                                shadowElevation = 0.dp
                            ) {
                                Text(
                                    text = title,
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    )
                }
            }

            when (val state = uiState) {
                is CommunityUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is CommunityUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = Color.Red)
                    }
                }
                is CommunityUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.posts) { post ->
                            PostItem(
                                post = post,
                                onClick = { onPostClick(post, false) },
                                onCommentClick = { onPostClick(post, true) },
                                onLikeClick = { viewModel.likePost(post.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

fun getAvatarResource(avatarName: String?): Int {
    return when (avatarName) {
        "avatar-1" -> R.drawable.avatar_1
        "avatar-2" -> R.drawable.avatar_2
        else -> R.drawable.default_avatar
    }
}

fun formatPostDate(dateStr: String?): String {
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

    return try {
        if (parsedDate != null) {
            val now = Date()
            val diff = now.time - parsedDate.time
            
            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24

            when {
                diff < 0 -> "Just now" // Future date (likely clock sync issue)
                seconds < 60 -> "Just now"
                minutes < 60 -> "$minutes min${if (minutes > 1) "s" else ""} ago"
                hours < 24 -> "$hours hour${if (hours > 1) "s" else ""} ago"
                days < 7 -> "$days day${if (days > 1) "s" else ""} ago"
                else -> {
                    val sdfOutput = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    sdfOutput.format(parsedDate)
                }
            }
        } else dateStr
    } catch (e: Exception) {
        dateStr
    }
}

@Composable
fun PostItem(
    post: PostResponse,
    onClick: () -> Unit,
    onCommentClick: () -> Unit,
    onLikeClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val displayName = if (post.anonymous) "Anonymous" else post.username
                val displayAvatar = if (post.anonymous) null else post.userProfilePicture
                val avatarRes = getAvatarResource(displayAvatar)
                
                androidx.compose.foundation.Image(
                    painter = painterResource(id = avatarRes),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = displayName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = formatPostDate(post.createdAt), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = post.content, fontSize = 14.sp)
            
            if (post.imageUrl != null) {
                Spacer(modifier = Modifier.height(12.dp))
                AsyncImage(
                    model = post.imageUrl,
                    contentDescription = "Post Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Like
                    IconButton(
                        onClick = onLikeClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (post.isLikedByMe) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (post.isLikedByMe) Color(0xFFFF4081) else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = post.likeCount.toString(),
                        fontSize = 14.sp,
                        color = if (post.isLikedByMe) Color(0xFFFF4081) else MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // Comment
                    IconButton(
                        onClick = onCommentClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = "Comment",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = post.commentCount.toString(), 
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                IconButton(onClick = { /* TODO: Bookmark */ }) {
                    Icon(
                        imageVector = Icons.Outlined.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PostItemPreview() {
    PostItem(
        post = PostResponse(
            id = 1,
            content = "Some days are harder than others. Remember, it's okay to not be okay. 💜",
            imageUrl = null,
            username = "HopefulSoul",
            userProfilePicture = null,
            likeCount = 24,
            commentCount = 6,
            isLikedByMe = true,
            createdAt = "2 hours ago",
            anonymous = false
        ),
        onClick = {},
        onCommentClick = {},
        onLikeClick = {}
    )
}

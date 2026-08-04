package com.serenemind.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.serenemind.R
import com.serenemind.ui.theme.*
import com.serenemind.util.getAvatarResource

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    isDarkMode: Boolean,
    onDarkModeToggle: (Boolean) -> Unit,
    onNavigateToSettings: () -> Unit = {},
    onLogout: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    onNavigateToSavedPosts: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Custom Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.size(48.dp))
                Text("My Profile", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                IconButton(onClick = onNavigateToSettings) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
                }
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when (val state = uiState) {
                    is ProfileUiState.Loading -> CircularProgressIndicator()
                    is ProfileUiState.Error -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(state.message, color = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { viewModel.fetchUserProfile() }) {
                                Text("Retry")
                            }
                        }
                    }
                    is ProfileUiState.Success -> {
                        val user = state.user
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Profile Banner Card
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(
                                        Brush.linearGradient(
                                            colors = if (isDarkMode) {
                                                listOf(Color(0xFF1A1A2E), Color(0xFF2A2A4E))
                                            } else {
                                                listOf(Color(0xFFE3F2FD), Color(0xFFEDE7F6))
                                            }
                                        )
                                    )
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = painterResource(id = getAvatarResource(user.avatar)),
                                        contentDescription = "User Avatar",
                                        modifier = Modifier
                                            .size(72.dp)
                                            .clip(CircleShape)
                                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = user.fullname ?: "User",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDarkMode) Color.White else Color.Black
                                        )
                                        Text(
                                            text = user.email ?: "",
                                            fontSize = 14.sp,
                                            color = if (isDarkMode) Color.LightGray else Color.Gray
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "\"Be kind to your mind.\"",
                                            fontSize = 13.sp,
                                            color = if (isDarkMode) MaterialTheme.colorScheme.primary else Color(0xFF673AB7)
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Profile",
                                        tint = if (isDarkMode) Color.LightGray else Color.Gray,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Your Activity Section
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    "Your Activity",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 8.dp, bottom = 12.dp),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(24.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isDarkMode) Color(0xFF333333) else Color(0xFFF0F0F0))
                                ) {
                                    val activity = state.activity
                                    Row(
                                        modifier = Modifier.padding(vertical = 20.dp).fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceAround
                                    ) {
                                        ActivityItem(
                                            icon = Icons.AutoMirrored.Filled.Assignment, 
                                            label = "Journals", 
                                            value = activity?.totalJournals?.toString() ?: "0", 
                                            subValue = "entries", 
                                            color = Color(0xFF9C27B0)
                                        )
                                        ActivityItem(
                                            icon = Icons.Default.Flag, 
                                            label = "Goals Completed", 
                                            value = activity?.goalsCompleted?.toString() ?: "0", 
                                            subValue = "goals", 
                                            color = Color(0xFF4CAF50)
                                        )
                                        ActivityItem(
                                            icon = Icons.Default.Create, 
                                            label = "Posts", 
                                            value = activity?.totalPosts?.toString() ?: "0", 
                                            subValue = "posts", 
                                            color = Color(0xFFFF9800)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Settings List
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Column {
                                    ProfileMenuItem(
                                        icon = Icons.Default.Person, 
                                        title = "Personal Information",
                                        iconTint = Color.Gray
                                    )
                                    ProfileMenuItem(
                                        icon = Icons.Default.Bookmark, 
                                        title = "Saved Post",
                                        iconTint = Color(0xFF2196F3),
                                        onClick = onNavigateToSavedPosts
                                    )

                                    // Dark Mode Toggle
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.DarkMode,
                                                contentDescription = null,
                                                tint = Color(0xFF7C4DFF),
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(modifier = Modifier.width(16.dp))
                                            Text(
                                                text = "Dark Mode",
                                                fontSize = 15.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                        Switch(
                                            checked = isDarkMode,
                                            onCheckedChange = onDarkModeToggle
                                        )
                                    }
                                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                                    ProfileMenuItem(
                                        icon = Icons.Default.Info, 
                                        title = "About SereneMind",
                                        iconTint = Color(0xFF2196F3),
                                        onClick = onNavigateToAbout
                                    )
                                    ProfileMenuItem(
                                        icon = Icons.AutoMirrored.Filled.Logout,
                                        title = "Logout",
                                        iconTint = Color(0xFFF44336),
                                        isLast = true,
                                        onClick = {
                                            viewModel.logout { onLogout() }
                                        }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityItem(icon: ImageVector, label: String, value: String, subValue: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = color.copy(alpha = 0.15f),
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon, 
                    contentDescription = null, 
                    tint = color, 
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = label, 
            fontSize = 11.sp, 
            color = MaterialTheme.colorScheme.onSurfaceVariant, 
            textAlign = TextAlign.Center,
            lineHeight = 14.sp,
            modifier = Modifier.height(28.dp)
        )
        Text(
            text = value, 
            fontSize = 18.sp, 
            fontWeight = FontWeight.Bold, 
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = subValue, 
            fontSize = 11.sp, 
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    badge: String? = null,
    isLast: Boolean = false,
    iconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: () -> Unit = {}
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            if (badge != null) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(
                        text = badge,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Go",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(14.dp)
            )
        }
        if (!isLast) {
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        }
    }
}

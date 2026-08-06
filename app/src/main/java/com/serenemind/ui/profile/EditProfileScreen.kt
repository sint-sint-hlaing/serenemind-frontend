package com.serenemind.ui.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.serenemind.R
import com.serenemind.ui.theme.TextPrimary
import com.serenemind.ui.theme.TextSecondary
import com.serenemind.util.getAvatarResource
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    val userProfile by viewModel.userProfile.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var fullname by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var birthday by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(Unit) {
        viewModel.fetchUserProfile()
    }

    LaunchedEffect(userProfile) {
        userProfile?.let {
            fullname = it.fullname ?: ""
            username = it.username ?: ""
            email = it.email ?: ""
            bio = it.bio ?: ""
            location = it.location ?: ""
            
            // Normalize birthday format: try to handle yyyy.M.d or yyyy-M-d to yyyy-MM-dd
            val rawBirthday = it.birthday ?: ""
            birthday = if (rawBirthday.contains(".") || (rawBirthday.contains("-") && rawBirthday.length < 10)) {
                try {
                    val parts = rawBirthday.split(".", "-")
                    if (parts.size == 3) {
                        val y = parts[0]
                        val m = parts[1].padStart(2, '0')
                        val d = parts[2].padStart(2, '0')
                        "$y-$m-$d"
                    } else rawBirthday
                } catch (_: Exception) { rawBirthday }
            } else {
                rawBirthday
            }
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is EditProfileUiState.Success) {
            onSuccess()
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    // Date Picker Logic
    val calendar = Calendar.getInstance()
    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            birthday = formatter.format(selectedDate.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Edit Profile", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = {
                        viewModel.updateProfile(context, fullname, username, birthday, bio, selectedImageUri)
                    }) {
                        Text("Save", color = Color(0xFF7E57C2), fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar Section
            Box(contentAlignment = Alignment.BottomEnd) {
                Surface(
                    modifier = Modifier.size(120.dp),
                    shape = CircleShape,
                    border = BorderStroke(4.dp, Color(0xFFD1C4E9))
                ) {
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        AsyncImage(
                            model = userProfile?.avatar,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Crop,
                            error = painterResource(id = getAvatarResource(null))
                        )
                    }
                }
                Surface(
                    modifier = Modifier.size(36.dp),
                    shape = CircleShape,
                    color = Color(0xFF7E57C2),
                    border = BorderStroke(2.dp, Color.White),
                    onClick = { launcher.launch("image/*") }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Change Avatar",
                color = Color(0xFF7E57C2),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.clickable { launcher.launch("image/*") }
            )
            Text(
                text = "JPG or PNG. Max size 2MB",
                color = Color.Gray,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Information Fields
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Profile Information",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                EditField(
                    icon = Icons.Outlined.Person,
                    label = "Full Name",
                    value = fullname,
                    onValueChange = { fullname = it }
                )
                EditField(
                    icon = Icons.Outlined.AlternateEmail,
                    label = "Username",
                    value = username,
                    onValueChange = { username = it }
                )
                EditField(
                    icon = Icons.Outlined.Email,
                    label = "Email",
                    value = email,
                    onValueChange = { },
                    readOnly = true
                )
                EditField(
                    icon = Icons.Outlined.Edit,
                    label = "Bio",
                    value = bio,
                    onValueChange = { bio = it }
                )
                EditField(
                    icon = Icons.Outlined.LocationOn,
                    label = "Location",
                    value = location,
                    onValueChange = { location = it }
                )
                EditField(
                    icon = Icons.Outlined.CalendarToday,
                    label = "Birthday",
                    value = birthday,
                    onValueChange = { birthday = it },
                    showArrow = true,
                    onClick = { datePickerDialog.show() },
                    readOnly = true
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quote Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFF3E5F5),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        color = Color(0xFFEDE7F6)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Eco, contentDescription = null, tint = Color(0xFF7E57C2), modifier = Modifier.size(20.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = if (bio.isNotBlank()) bio else "A kind mind creates a happy life.",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF7E57C2)
                        )
                        Text(
                            text = "Be kind to your mind. Every day.",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Buttons
            Button(
                onClick = {
                    viewModel.updateProfile(context, fullname, username, birthday, bio, selectedImageUri)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7E57C2))
            ) {
                if (uiState is EditProfileUiState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Save Changes", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onBack) {
                Text("Cancel", color = Color(0xFF7E57C2), fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun EditField(
    icon: ImageVector,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    readOnly: Boolean = false,
    showArrow: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .let { if (onClick != null) it.clickable { onClick() } else it },
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFF5F5F5)),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF7E57C2), modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(label, fontSize = 11.sp, color = Color.Gray)
                if (readOnly) {
                    Text(
                        text = value, 
                        fontSize = 14.sp, 
                        fontWeight = FontWeight.Medium, 
                        color = if (onClick != null) Color.Black else Color.Gray
                    )
                } else {
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            if (showArrow) {
                Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
            }
        }
    }
}

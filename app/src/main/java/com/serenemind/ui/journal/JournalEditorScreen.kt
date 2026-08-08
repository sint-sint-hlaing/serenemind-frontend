package com.serenemind.ui.journal

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun JournalEditorScreen(
    id: Int?,
    viewModel: JournalEditorViewModel,
    isDarkMode: Boolean,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val journal by viewModel.journal.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var title by remember { mutableStateOf("") }
    val richTextState = rememberRichTextState()
    var isFavourite by remember { mutableStateOf(false) }
    var tagInput by remember { mutableStateOf("") }
    val tags = remember { mutableStateListOf<String>() }
    
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var existingPhotoUrl by remember { mutableStateOf<String?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    LaunchedEffect(id) {
        if (id != null) {
            viewModel.loadJournal(id)
        }
    }

    LaunchedEffect(journal) {
        journal?.let {
            title = it.title
            richTextState.setHtml(it.content)
            isFavourite = it.favourite
            existingPhotoUrl = it.photoUrl
            tags.clear()
            it.tags?.let { t -> tags.addAll(t) }
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        if (uiState is JournalEditorUiState.Success) {
            onNavigateBack()
        } else if (uiState is JournalEditorUiState.Error) {
            snackbarHostState.showSnackbar((uiState as JournalEditorUiState.Error).message)
        }
    }

    Scaffold(
        snackbarHost = {
            Box(Modifier.fillMaxSize()) {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        },
        topBar = {
            TopAppBar(
                title = { Text(if (id == null) "New Journal" else "Edit Journal", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (title.isBlank()) {
                                viewModel.setError("Please enter a title")
                                return@IconButton
                            }
                            if (richTextState.annotatedString.text.isBlank()) {
                                viewModel.setError("Please write something in your journal")
                                return@IconButton
                            }

                            val photoPart = selectedImageUri?.let { uri ->
                                prepareFilePart(context, "photo", uri)
                            }

                            viewModel.saveJournal(
                                id = id,
                                title = title,
                                content = richTextState.annotatedString.text,
                                tags = tags.toList(),
                                favourite = isFavourite,
                                photoPart = photoPart
                            )
                        }
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Save")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState is JournalEditorUiState.Loading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                TextField(
                    value = title,
                    onValueChange = { newTitle -> title = newTitle },
                    placeholder = { Text("Title", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    textStyle = LocalTextStyle.current.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Image Display - Industry standard: below title, above content
                if (selectedImageUri != null || existingPhotoUrl != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        AsyncImage(
                            model = selectedImageUri ?: existingPhotoUrl,
                            contentDescription = "Journal Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .size(32.dp),
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.5f)
                        ) {
                            IconButton(
                                onClick = { 
                                    selectedImageUri = null
                                    existingPhotoUrl = null
                                }
                            ) {
                                Icon(
                                    Icons.Default.Close, 
                                    contentDescription = "Remove Photo", 
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    val currentStyle = richTextState.currentSpanStyle

                    IconButton(
                        onClick = { richTextState.toggleSpanStyle(SpanStyle(fontWeight = FontWeight.Bold)) },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = if (currentStyle.fontWeight == FontWeight.Bold) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    ) { Icon(Icons.Default.FormatBold, "Bold") }
                    
                    IconButton(
                        onClick = { richTextState.toggleSpanStyle(SpanStyle(fontStyle = FontStyle.Italic)) },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = if (currentStyle.fontStyle == FontStyle.Italic) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    ) { Icon(Icons.Default.FormatItalic, "Italic") }
                    
                    IconButton(
                        onClick = { richTextState.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.Underline)) },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = if (currentStyle.textDecoration?.contains(TextDecoration.Underline) == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    ) { Icon(Icons.Default.FormatUnderlined, "Underline") }

                    IconButton(
                        onClick = { richTextState.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = if (currentStyle.textDecoration?.contains(TextDecoration.LineThrough) == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    ) { Icon(Icons.Default.FormatStrikethrough, "Strikethrough") }
                }

                HorizontalDivider()

                RichTextEditor(
                    state = richTextState,
                    placeholder = { Text("What's on your mind?") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp),
                    colors = RichTextEditorDefaults.richTextEditorColors(
                        containerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Tags", fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        value = tagInput,
                        onValueChange = { newTag -> tagInput = newTag },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Add tag...") },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )
                    IconButton(onClick = {
                        if (tagInput.isNotBlank()) {
                            tags.add(tagInput.trim())
                            tagInput = ""
                        }
                    }) {
                        Icon(Icons.Default.Add, "Add tag")
                    }
                }
                
                androidx.compose.foundation.layout.FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tags.forEach { tag ->
                        AssistChip(
                            onClick = { tags.remove(tag) },
                            label = { Text(tag) },
                            trailingIcon = { Icon(Icons.Default.Close, null, modifier = Modifier.size(16.dp)) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(onClick = { imagePickerLauncher.launch("image/*") }) { 
                        Icon(Icons.Default.PhotoCamera, "Photo") 
                    }
                    
                    IconButton(onClick = { isFavourite = !isFavourite }) {
                        Icon(
                            if (isFavourite) Icons.Default.Star else Icons.Default.StarOutline,
                            contentDescription = "Favorite",
                            tint = if (isFavourite) Color(0xFFFFD700) else Color.Gray
                        )
                    }
                }
            }
        }
    }
}

fun prepareFilePart(context: android.content.Context, partName: String, fileUri: Uri): MultipartBody.Part? {
    return try {
        val type = context.contentResolver.getType(fileUri) ?: "image/jpeg"
        val extension = if (type == "image/png") "png" else "jpg"
        
        val file = context.contentResolver.openInputStream(fileUri)?.use { inputStream ->
            val tempFile = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.$extension")
            FileOutputStream(tempFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            tempFile
        }
        
        file?.let {
            val requestFile = it.asRequestBody(type.toMediaTypeOrNull())
            MultipartBody.Part.createFormData(partName, it.name, requestFile)
        }
    } catch (e: Exception) {
        Log.e("JournalEditor", "prepareFilePart failed", e)
        null
    }
}

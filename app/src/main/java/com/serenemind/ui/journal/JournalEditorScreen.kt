package com.serenemind.ui.journal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalEditorScreen(
    id: Int?,
    viewModel: JournalEditorViewModel,
    onNavigateBack: () -> Unit
) {
    val journal by viewModel.journal.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var title by remember { mutableStateOf("") }
    val richTextState = rememberRichTextState()
    var isPrivate by remember { mutableStateOf(false) }
    var isFavourite by remember { mutableStateOf(false) }
    var tagInput by remember { mutableStateOf("") }
    val tags = remember { mutableStateListOf<String>() }

    LaunchedEffect(id) {
        if (id != null) {
            viewModel.loadJournal(id)
        }
    }

    LaunchedEffect(journal) {
        journal?.let {
            title = it.title
            // Set content to rich text state
            // If it's HTML, set it as HTML. If it's plain text, it still works.
            richTextState.setHtml(it.content)
            isPrivate = it.isPrivate
            isFavourite = it.favourite
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                            viewModel.saveJournal(
                                id = id,
                                title = title,
                                // Send plain text if backend no longer wants HTML
                                content = richTextState.annotatedString.text,
                                tags = if (tags.isEmpty()) null else tags.toList(),
                                isPrivate = isPrivate,
                                favourite = isFavourite
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
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
                    onValueChange = { title = it },
                    placeholder = { Text("Title", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    textStyle = LocalTextStyle.current.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Rich Text Toolbar
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
                        .weight(1f),
                    colors = RichTextEditorDefaults.richTextEditorColors(
                        containerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Tags Section
                Text("Tags", fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        value = tagInput,
                        onValueChange = { tagInput = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Add tag...") },
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
                
                FlowRow(
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
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { /* Photo */ }) { Icon(Icons.Default.PhotoCamera, "Photo") }
                        IconButton(onClick = { isFavourite = !isFavourite }) {
                            Icon(
                                if (isFavourite) Icons.Default.Star else Icons.Default.StarOutline,
                                contentDescription = "Favorite",
                                tint = if (isFavourite) Color(0xFFFFD700) else Color.Gray
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Private", fontSize = 14.sp)
                        Switch(
                            checked = isPrivate,
                            onCheckedChange = { isPrivate = it }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = verticalArrangement
    ) {
        content()
    }
}

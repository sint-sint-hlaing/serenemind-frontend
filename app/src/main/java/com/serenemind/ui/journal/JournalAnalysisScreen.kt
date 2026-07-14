package com.serenemind.ui.journal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.serenemind.model.response.JournalAnalysisResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalAnalysisScreen(
    id: Int,
    viewModel: JournalAnalysisViewModel,
    onNavigateBack: () -> Unit
) {
    val analysis by viewModel.analysis.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(id) {
        viewModel.loadAnalysis(id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Journal Analysis", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState is JournalAnalysisUiState.Loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState is JournalAnalysisUiState.Error) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = (uiState as JournalAnalysisUiState.Error).message, color = MaterialTheme.colorScheme.error)
            }
        } else if (analysis != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                AnalysisHeader()

                Spacer(modifier = Modifier.height(24.dp))

                AnalysisSection("Your Emotion", analysis!!.emotion ?: "Neutral")
                LinearProgressIndicator(
                    progress = 0.72f, // Mocked percentage for now
                    modifier = Modifier.fillMaxWidth().height(8.dp).padding(vertical = 8.dp),
                    color = Color(0xFF4CAF50),
                    trackColor = Color(0xFFE8F5E9)
                )

                Spacer(modifier = Modifier.height(16.dp))

                AnalysisSection("Stress Level", analysis!!.stressLevel ?: "Low")
                LinearProgressIndicator(
                    progress = (analysis!!.stressScore ?: 0).toFloat() / 100f,
                    modifier = Modifier.fillMaxWidth().height(8.dp).padding(vertical = 8.dp),
                    color = Color(0xFFFFC107),
                    trackColor = Color(0xFFFFF8E1)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text("AI Response", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    text = analysis!!.aiResponse ?: "",
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Key Themes", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    analysis!!.keyThemes?.forEach { theme ->
                        SuggestionChip(onClick = {}, label = { Text(theme) })
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF7B1FA2))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("AI Suggestion", fontWeight = FontWeight.Bold, color = Color(0xFF7B1FA2))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = analysis!!.aiSuggestion ?: "No suggestions yet.",
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.FavoriteBorder, contentDescription = "Like")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnalysisHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Icon(
            Icons.Default.AutoAwesome,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = Color(0xFF6750A4)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("Analysis Complete", fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}

@Composable
fun AnalysisSection(title: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Gray)
    }
}

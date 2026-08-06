package com.serenemind.ui.meditation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeditationTimerScreen(
    viewModel: MeditationViewModel,
    onBack: () -> Unit
) {
    val meditation by viewModel.selectedMeditation.collectAsState()
    var selectedMinutes by remember { mutableIntStateOf(10) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Set Timer", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "End meditation in",
                fontSize = 18.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "$selectedMinutes Minutes",
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Slider(
                value = selectedMinutes.toFloat(),
                onValueChange = { selectedMinutes = it.toInt() },
                valueRange = 1f..60f,
                steps = 59
            )
            
            Spacer(modifier = Modifier.height(64.dp))
            
            Button(
                onClick = {
                    meditation?.let {
                        viewModel.saveTimer(it.id, selectedMinutes) { msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            onBack()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Set Sleep Timer", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

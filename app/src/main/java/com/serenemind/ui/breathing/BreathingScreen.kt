package com.serenemind.ui.breathing

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.serenemind.R
import com.serenemind.model.response.BreathingStartResponse
import com.serenemind.model.response.BreathingSummaryResponse
import kotlinx.coroutines.delay

@Composable
fun BreathingScreen(
    viewModel: BreathingViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is BreathingUiState.Idle -> {
                BreathingSelectionContent(
                    onStart = { type, duration -> viewModel.startSession(type, duration) },
                    onBack = onBack
                )
            }
            is BreathingUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF6750A4))
                }
            }
            is BreathingUiState.SessionStarted -> {
                BreathingExerciseContent(
                    sessionData = state.response,
                    onComplete = { viewModel.completeSession(state.response.sessionId) },
                    onTrackRound = { round -> viewModel.trackRound(state.response.sessionId, round) },
                    onExit = { 
                        viewModel.resetToIdle()
                        onBack()
                    }
                )
            }
            is BreathingUiState.SessionSummary -> {
                BreathingSummaryContent(
                    summary = state.summary,
                    onDone = { 
                        viewModel.resetToIdle()
                        onBack()
                    },
                    onDoAgain = { viewModel.resetToIdle() }
                )
            }
            is BreathingUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(state.message, color = Color.Red)
                        Button(onClick = { viewModel.resetToIdle() }) {
                            Text("Try Again")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreathingSelectionContent(
    onStart: (String, Int) -> Unit,
    onBack: () -> Unit
) {
    var selectedExercise by remember { mutableStateOf("BOX_BREATHING") }
    var selectedDuration by remember { mutableStateOf(3) }

    val exercises = listOf(
        BreathingType("BOX_BREATHING", "Box Breathing", "4 steps - 4 seconds each", R.drawable.ic_launcher_foreground), // Replace with proper icons
        BreathingType("BREATH_478", "4-7-8 Breathing", "Relaxing - Sleep support", R.drawable.ic_launcher_foreground),
        BreathingType("CALM_BREATH", "Calm Breathing", "Natural - Stress relief", R.drawable.ic_launcher_foreground)
    )

    val durations = listOf(1, 3, 5, 10)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Breathing", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back", modifier = Modifier.size(20.dp))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Take a deep breath and find your calm.",
                color = Color.Gray,
                fontSize = 14.sp
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Illustration placeholder
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF3E5F5)),
                contentAlignment = Alignment.Center
            ) {
                // Image(painter = painterResource(id = R.drawable.breathing_illus), contentDescription = null)
                Text("🧘‍♀️", fontSize = 80.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Choose your exercise", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Icon(Icons.Default.Info, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            exercises.forEach { type ->
                ExerciseItem(
                    type = type,
                    isSelected = selectedExercise == type.id,
                    onClick = { selectedExercise = type.id }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Session duration",
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                durations.forEach { duration ->
                    DurationChip(
                        duration = duration,
                        isSelected = selectedDuration == duration,
                        onClick = { selectedDuration = duration },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onStart(selectedExercise, selectedDuration) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4))
            ) {
                Text("Start Breathing", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

data class BreathingType(val id: String, val name: String, val desc: String, val icon: Int)

@Composable
fun ExerciseItem(type: BreathingType, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        border = if (isSelected) BorderStroke(2.dp, Color(0xFF6750A4)) else BorderStroke(1.dp, Color(0xFFEEEEEE)),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF3E5F5)),
                contentAlignment = Alignment.Center
            ) {
                // Icon(painter = painterResource(id = type.icon), contentDescription = null)
                Text(if (type.id == "BOX_BREATHING") "🔲" else if (type.id == "BREATH_478") "〰️" else "🍃")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(type.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(type.desc, color = Color.Gray, fontSize = 13.sp)
            }
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF6750A4))
            )
        }
    }
}

@Composable
fun DurationChip(duration: Int, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color(0xFF6750A4) else Color(0xFFF7F7F7),
        border = if (isSelected) null else BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Box(modifier = Modifier.padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
            Text(
                "${duration} min",
                color = if (isSelected) Color.White else Color.Black,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 14.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreathingExerciseContent(
    sessionData: BreathingStartResponse,
    onComplete: () -> Unit,
    onTrackRound: (Int) -> Unit,
    onExit: () -> Unit
) {
    var isPaused by remember { mutableStateOf(false) }
    var currentRound by remember { mutableStateOf(1) }
    var currentPhase by remember { mutableStateOf(BreathingPhase.INHALE) }
    var timeLeftInPhase by remember { mutableStateOf(4) }
    var totalSecondsElapsed by remember { mutableStateOf(0) }

    val exerciseName = when (sessionData.exerciseType) {
        "BOX_BREATHING" -> "Box Breathing"
        "BREATH_478" -> "4-7-8 Breathing"
        else -> "Calm Breathing"
    }

    // Animation for the circle
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    LaunchedEffect(isPaused) {
        while (!isPaused && totalSecondsElapsed < sessionData.totalDurationSeconds) {
            delay(1000)
            timeLeftInPhase--
            totalSecondsElapsed++
            
            if (timeLeftInPhase <= 0) {
                // Switch phase
                when (sessionData.exerciseType) {
                    "BOX_BREATHING" -> {
                        when (currentPhase) {
                            BreathingPhase.INHALE -> { currentPhase = BreathingPhase.HOLD; timeLeftInPhase = 4 }
                            BreathingPhase.HOLD -> { currentPhase = BreathingPhase.EXHALE; timeLeftInPhase = 4 }
                            BreathingPhase.EXHALE -> { currentPhase = BreathingPhase.HOLD_EMPTY; timeLeftInPhase = 4 }
                            BreathingPhase.HOLD_EMPTY -> { 
                                currentPhase = BreathingPhase.INHALE; timeLeftInPhase = 4
                                currentRound++
                                onTrackRound(currentRound)
                            }
                        }
                    }
                    "BREATH_478" -> {
                        when (currentPhase) {
                            BreathingPhase.INHALE -> { currentPhase = BreathingPhase.HOLD; timeLeftInPhase = 7 }
                            BreathingPhase.HOLD -> { currentPhase = BreathingPhase.EXHALE; timeLeftInPhase = 8 }
                            BreathingPhase.EXHALE -> { 
                                currentPhase = BreathingPhase.INHALE; timeLeftInPhase = 4
                                currentRound++
                                onTrackRound(currentRound)
                            }
                            else -> {}
                        }
                    }
                    else -> { // Calm
                         when (currentPhase) {
                            BreathingPhase.INHALE -> { currentPhase = BreathingPhase.EXHALE; timeLeftInPhase = 6 }
                            BreathingPhase.EXHALE -> { 
                                currentPhase = BreathingPhase.INHALE; timeLeftInPhase = 4
                                currentRound++
                                onTrackRound(currentRound)
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
        if (totalSecondsElapsed >= sessionData.totalDurationSeconds) {
            onComplete()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(exerciseName, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onExit) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Exit", modifier = Modifier.size(20.dp))
                    }
                },
                actions = {
                    IconButton(onClick = { /* Info */ }) {
                        Icon(Icons.Default.Info, contentDescription = "Info")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Round $currentRound of ${sessionData.estimatedRounds}", color = Color.Gray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(sessionData.estimatedRounds) { i ->
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (i < currentRound) Color(0xFF6750A4) else Color(0xFFEEEEEE))
                        )
                    }
                }
            }

            // Breathing Animation
            Box(contentAlignment = Alignment.Center) {
                // Outer circle (Stroke)
                Canvas(modifier = Modifier.size(250.dp)) {
                    drawCircle(
                        color = Color(0xFFEADDFF),
                        style = Stroke(width = 4.dp.toPx())
                    )
                }
                
                // Pulsing inner circle
                Box(
                    modifier = Modifier
                        .size(180.dp * (if (currentPhase == BreathingPhase.INHALE) scale else 1.0f))
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color(0xFFD0BCFF), Color(0xFFEADDFF))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = currentPhase.label,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF21005D)
                        )
                        Text(
                            text = "${timeLeftInPhase}s",
                            fontSize = 24.sp,
                            color = Color(0xFF21005D)
                        )
                    }
                }
            }

            Text(
                text = currentPhase.instruction,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                fontSize = 16.sp,
                color = Color.Gray
            )

            Button(
                onClick = { isPaused = !isPaused },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3E5F5), contentColor = Color(0xFF6750A4)),
                modifier = Modifier.width(120.dp)
            ) {
                Icon(if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isPaused) "Resume" else "Pause", fontWeight = FontWeight.Bold)
            }
        }
    }
}

enum class BreathingPhase(val label: String, val instruction: String) {
    INHALE("Inhale", "Breathe in slowly through your nose."),
    HOLD("Hold", "Hold your breath."),
    EXHALE("Exhale", "Breathe out slowly through your mouth."),
    HOLD_EMPTY("Hold", "Hold your breath before inhaling again.")
}

@Composable
fun BreathingSummaryContent(
    summary: BreathingSummaryResponse,
    onDone: () -> Unit,
    onDoAgain: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color(0xFF6750A4)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Well done!", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("You've completed the exercise.", color = Color.Gray)

        Spacer(modifier = Modifier.height(40.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFFFBFBFF),
            border = BorderStroke(1.dp, Color(0xFFEEEEEE))
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Session Summary", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    SummaryItem(label = "Duration", value = summary.duration, icon = "⏱️")
                    SummaryItem(label = "Rounds", value = "${summary.rounds}", icon = "🔄")
                    SummaryItem(label = "Total breaths", value = "${summary.totalBreaths}", icon = "🍃")
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = onDone,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4))
        ) {
            Text("Done", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onDoAgain) {
            Text("Do Again", color = Color(0xFF6750A4), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SummaryItem(label: String, value: String, icon: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(icon, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(label, color = Color.Gray, fontSize = 12.sp)
    }
}

package com.serenemind.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.serenemind.R

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit,
    onLoginClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(50.dp)) // Reduced from 80.dp to move content up

            // Logo
            Icon(
                painter = painterResource(id = R.drawable.ic_launcher_foreground), // Placeholder for the tree/leaf logo
                contentDescription = "Logo",
                modifier = Modifier.size(100.dp),
                tint = Color(0xFF6750A4)
            )

            Text(
                text = "SereneMind",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6750A4)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Take care of your mind.\nEvery day.",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = Color(0xFF6750A4).copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.weight(1f))

            // Buttons
            Button(
                onClick = onGetStarted,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4))
            ) {
                Text("Get Started", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF6750A4))
            ) {
                Text("Login", fontSize = 16.sp, color = Color(0xFF6750A4), fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

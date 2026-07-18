package com.serenemind

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.serenemind.datastore.ThemeManager
import com.serenemind.datastore.TokenManager
import com.serenemind.navigation.AppNavigation
import com.serenemind.network.NetworkModule
import com.serenemind.repository.AuthRepository
import com.serenemind.ui.login.LoginViewModel
import com.serenemind.ui.theme.SerenemindclientTheme
import androidx.compose.runtime.*
import androidx.compose.foundation.isSystemInDarkTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Handle permission result if needed
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        askNotificationPermission()
        createReminderNotificationChannel()

        val tokenManager = TokenManager(this)
        val api = NetworkModule.provideApiService(this, tokenManager)
        val repo = AuthRepository(api)
        val themeManager = ThemeManager(this)

        val viewModel = LoginViewModel(repo, tokenManager)

        setContent {
            val systemTheme = isSystemInDarkTheme()
            val themePreference by themeManager.isDarkMode.collectAsState(initial = null)
            val scope = rememberCoroutineScope()
            
            val isDarkMode = themePreference ?: systemTheme

            SerenemindclientTheme(darkTheme = isDarkMode) {
                AppNavigation(
                    loginViewModel = viewModel, 
                    tokenManager = tokenManager,
                    isDarkMode = isDarkMode,
                    onDarkModeToggle = { enabled ->
                        scope.launch { themeManager.setDarkMode(enabled) }
                    }
                )
            }
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun createReminderNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "reminder_channel_v2"
            val name = "Mental Health Reminders"
            val descriptionText = "Gentle reminders for your wellness"
            val importance = android.app.NotificationManager.IMPORTANCE_HIGH

            val channel = android.app.NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
                enableLights(true)
                enableVibration(true)
                
                val defaultSoundUri = android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION)
                val audioAttributes = android.media.AudioAttributes.Builder()
                    .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(android.media.AudioAttributes.USAGE_NOTIFICATION)
                    .build()
                setSound(defaultSoundUri, audioAttributes)
            }

            val notificationManager = getSystemService(android.content.Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.data.PreferencesManager
import com.example.service.ImageGenerationService
import com.example.ui.HomeScreen
import com.example.ui.SettingsScreen
import com.example.ui.theme.LeatherBlack
import com.example.ui.theme.MyApplicationTheme

enum class AppScreen {
  HOME,
  SETTINGS
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val prefsManager = PreferencesManager(applicationContext)
    val imageService = ImageGenerationService(applicationContext)

    setContent {
      MyApplicationTheme {
        Surface(
          modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(),
          color = LeatherBlack
        ) {
          var currentScreen by remember { mutableStateOf(AppScreen.HOME) }

          Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
            when (screen) {
              AppScreen.HOME -> {
                HomeScreen(
                  prefsManager = prefsManager,
                  imageService = imageService,
                  onNavigateToSettings = { currentScreen = AppScreen.SETTINGS }
                )
              }
              AppScreen.SETTINGS -> {
                SettingsScreen(
                  prefsManager = prefsManager,
                  onNavigateBack = { currentScreen = AppScreen.HOME }
                )
              }
            }
          }
        }
      }
    }
  }
}

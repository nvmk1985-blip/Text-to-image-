package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val LuxuryDarkColorScheme = darkColorScheme(
  primary = GoldPrimary,
  onPrimary = TextOnGold,
  primaryContainer = GoldDark,
  onPrimaryContainer = GoldBright,
  secondary = CyanPrimary,
  onSecondary = TextOnCyan,
  secondaryContainer = CyanDark,
  onSecondaryContainer = CyanLight,
  tertiary = SkyBlue,
  background = LeatherBlack,
  onBackground = TextPrimary,
  surface = CardBg,
  onSurface = TextPrimary,
  surfaceVariant = CardSecondary,
  onSurfaceVariant = TextSecondary,
  outline = BorderDefault,
  outlineVariant = BorderSubtle,
)

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = LuxuryDarkColorScheme,
    typography = Typography,
    content = content
  )
}

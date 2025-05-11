package com.example.pawpin_v2.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun DogWalkerAppTheme(content: @Composable () -> Unit) {
    // You can use dynamic color on Android 12+ if desired
    val darkTheme = isSystemInDarkTheme()
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme  // define your ColorSchemes
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // define Typography if custom
        content = content
    )
}

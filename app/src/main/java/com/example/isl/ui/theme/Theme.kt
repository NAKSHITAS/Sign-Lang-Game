package com.example.isl.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Define your custom colors
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF2196F3),      // Blue (used for buttons like Log In, Attempt Sign)
    onPrimary = Color.White,          // Text on primary color
    background = Color(0xFFF9FAFB),   // App background (very light gray/white)
    onBackground = Color(0xFF111827), // Primary text color (dark gray)
    surface = Color.White,            // Cards or surfaces
    onSurface = Color(0xFF1F2937),    // Text/icons on surfaces
    secondary = Color(0xFF9CA3AF),    // Placeholder text color (e.g., Email or Username)
    onSecondary = Color.Black,
    outline = Color(0xFFE5E7EB)       // Light borders (e.g., divider, text field borders)
)

@Composable
fun ISLTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Currently only Light theme is defined. You can add dark theme if needed.
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,     // You can define custom typography separately
        shapes = Shapes,             // Customize shapes if needed
        content = content
    )
}

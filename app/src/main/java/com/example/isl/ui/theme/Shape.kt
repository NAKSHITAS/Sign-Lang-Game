package com.example.isl.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    small = RoundedCornerShape(8.dp),    // TextFields, Chips
    medium = RoundedCornerShape(12.dp),  // Buttons, Cards
    large = RoundedCornerShape(16.dp)    // Sheets or containers
)

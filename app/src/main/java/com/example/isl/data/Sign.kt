package com.example.isl.data

// Game's Sign
data class Sign(
    val id: String = "",
    val symbol: String = ""
)
data class SignMedia(
    val signId: String,
    val imageUrl: String,
    val videoUrl: String,
    val description: String
)


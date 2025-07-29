package com.example.isl.data

enum class MediaType {
    IMAGE,
    VIDEO
}

data class MediaItem(
    val id: String = "",
    val title: String = "",               // e.g., "Hello"
    val mediaUrl: String = "",            // Image or video URL
    val thumbnailUrl: String = "",        // For videos (optional)
    val mediaType: MediaType = MediaType.IMAGE,
)
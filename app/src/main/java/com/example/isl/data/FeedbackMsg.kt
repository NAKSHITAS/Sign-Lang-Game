package com.example.isl.data

data class FeedbackMessage(
    val message: String,
    val type: FeedbackType,
    val iconRes: Int? = null,
    val colorHex: String = "#FFFFFF",
    val playSound: Boolean = false
)

enum class FeedbackType {
    SUCCESS, WARNING, ERROR, INFO
}

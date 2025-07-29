package com.example.isl.data

data class Level(
    val levelId: Int = 1,
    val title: String = "",
    val description: String = "",
    val signs: List<Sign> = emptyList(),
    val sign_type : SignType = SignType.LETTER,
    val isUnlocked: Boolean = false,
    val isCompleted: Boolean = false,
    val reward: String = "",
    val difficulty: Difficulty = Difficulty.BEGINNER
)

enum class Difficulty {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED
}

enum class SignType {
    LETTER,
    WORD,
    SENTENCE
}

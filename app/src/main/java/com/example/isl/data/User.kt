package com.example.isl.data

data class User(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val levelUnlocked: Int = 1,
    val currentScore: Int = 0,
    val totalScore: Int = 0,
    val rewardsEarned: List<String> = emptyList(),
    val completedSigns: List<String> = emptyList(),
    val preferences: UserPreferences = UserPreferences(),
    val currentLevel: Int = 1,
    val currentLevelId: Int = 0,
    val isLevelCompleted: Boolean = false,
    val isGameOver: Boolean = false,

)
data class UserPreferences(
    val darkMode: Boolean = false
)
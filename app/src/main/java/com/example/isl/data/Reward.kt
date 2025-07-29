package com.example.isl.data

data class Reward(
    val rewardId: String = "",              // Unique reward ID
    val title: String = "",           // e.g. "Alphabet Ace"
    val description: String = "",     // e.g. "Completed all letter signs"
    val iconUrl: String = "",   // Optional: trophy/star/badge image
    val unlocked: Boolean = false,
    val rewardType: RewardType = RewardType.BADGE,
    val points: Int = 0,              // Coins/XP for this reward
    val awardedOn: Long = 0L          // Timestamp (for history/sorting)
)

enum class RewardType {
    BADGE,       // Static achievement
    TROPHY,      // Major milestone
    COIN,        // In-game currency
    XP,          // Experience points
    UNLOCK       // Unlocks new level or feature
}


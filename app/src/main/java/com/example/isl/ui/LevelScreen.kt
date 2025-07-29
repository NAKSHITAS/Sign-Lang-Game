package com.example.isl.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.isl.data.Level
import com.example.isl.data.SignType

@Composable
fun LevelsScreen(
    currentScreen: String,
    onScreenChange: (String) -> Unit,
    levels: List<Level>,
    currentLevel: Int,
    currentScore: Int,
    onLevelStart: (Level) -> Unit
) {
    Scaffold(
        bottomBar = { ISLBottomNavigationBar(currentScreen, onScreenChange) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LevelStatCard(currentLevel, "Current Level")
                LevelStatCard(currentScore, "Current Score")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Available Levels",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                items(levels) { level ->
                    LevelCard(level = level, onStart = { onLevelStart(level) })
                }
            }
        }
    }
}

@Composable
fun LevelCard(level: Level, onStart: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Level ${level.levelId}",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )
            Text(
                text = level.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = level.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onStart,
                    enabled = level.isUnlocked,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (level.isUnlocked) MaterialTheme.colorScheme.primary else Color.LightGray
                    ),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(text = if (level.isUnlocked) "Start" else "Locked 🔒")
                }

                if (level.isUnlocked) {
                    OutlinedButton(
                        onClick = {},
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(text = "Unlocked")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        AsyncImage(
            model = getLevelImage(level),
            contentDescription = level.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(16.dp))
        )
    }
}

fun getLevelImage(level: Level): String {
    return when (level.levelId) {
        1 -> "https://img.icons8.com/fluency/96/abc.png"
        2 -> "https://img.icons8.com/fluency/96/speech-bubble.png"
        3 -> "https://img.icons8.com/fluency/96/hand.png"
        4 -> "https://img.icons8.com/fluency/96/restaurant.png"
        else -> "https://img.icons8.com/fluency/96/book.png"
    }
}

@Preview(showBackground = true)
@Composable
fun LevelsScreenPreview() {
    val sampleLevels = listOf(
        Level(1, "Alphabets", "Learn ISL letters", sign_type = SignType.LETTER, isUnlocked = true),
        Level(2, "Words", "Common signs like hello, thank you", sign_type = SignType.WORD, isUnlocked = true),
        Level(3, "Gestures", "Sign full phrases and actions", sign_type = SignType.SENTENCE, isUnlocked = false),
        Level(4, "Food & Dining", "Learn food-related signs", isUnlocked = false)
    )

    LevelsScreen(
        currentScreen = "levels",
        onScreenChange = {},
        levels = sampleLevels,
        currentLevel = 2,
        currentScore = 120,
        onLevelStart = {}
    )
}

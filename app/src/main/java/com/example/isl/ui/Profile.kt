package com.example.isl.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.isl.R
import com.example.isl.data.Reward
import com.example.isl.viewmodel.UserViewModel
import androidx.compose.runtime.getValue


@Composable
fun ProfileScreen(
    currentScreen: String,
    onScreenChange: (String) -> Unit,
    rewards: List<Reward> = emptyList(),
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel = viewModel()
) {
    val userState by userViewModel.uiState.collectAsState()

    val userName = userState.name.ifEmpty { "Loading..." }
    val level = userState.currentLevel
    val score = userState.currentScore
    val levelsCompleted = userState.completedSigns.size

    Scaffold(
        bottomBar = {
            ISLBottomNavigationBar(currentScreen, onScreenChange)
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.avatar_placeholder),
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .border(3.dp, Color.Gray, CircleShape)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = userName,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Level $level",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LevelStatCard(score, "Score")
                LevelStatCard(levelsCompleted, "Levels Completed")
                LevelStatCard(rewards.count { it.unlocked }, "Badges Unlocked")
            }

            Spacer(modifier = Modifier.height(36.dp))

            AchievementsSection(rewards = rewards)
        }
    }
}


@Composable
fun LevelStatCard(value: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "$value",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun AchievementsSection(rewards: List<Reward>) {
    val unlockedRewards = rewards.filter { it.unlocked }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Achievements",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (unlockedRewards.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFFFF8E1)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(R.drawable.trophy_vector),
                        contentDescription = "Trophy",
                        tint = Color(0xFFFF9800),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "No badges yet!\nGo unlock some, champ!",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyRow {
                items(unlockedRewards) { reward ->
                    RewardCard(reward)
                }
            }
        }
    }
}

@Composable
fun RewardCard(reward: Reward) {
    Card(
        modifier = Modifier
            .padding(end = 16.dp)
            .width(120.dp)
            .height(140.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFDFDFD)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(12.dp)
        ) {
            AsyncImage(
                model = reward.iconUrl,
                contentDescription = reward.title,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = reward.title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ISLBottomNavigationBar(
    currentScreen: String,
    onScreenChange: (String) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentScreen == "profile",
            onClick = {
                if (currentScreen != "profile") onScreenChange("profile")
            },
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") }
        )
        NavigationBarItem(
            selected = currentScreen == "levels",
            onClick = {
                if (currentScreen != "levels") onScreenChange("levels")
            },
            icon = { Icon(Icons.Default.Star, contentDescription = "Levels") },
            label = { Text("Levels") }
        )
        NavigationBarItem(
            selected = currentScreen == "library",
            onClick = {
                if (currentScreen != "library") onScreenChange("library")
            },
            icon = { Icon(Icons.Default.VideoLibrary, contentDescription = "Library") },
            label = { Text("Library") }
        )
    }
}

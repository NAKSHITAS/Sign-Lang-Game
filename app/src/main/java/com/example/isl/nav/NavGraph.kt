package com.example.isl.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.isl.data.Level
import com.example.isl.data.MediaItem
import com.example.isl.ui.*

@Composable
fun NavGraph(
    navController: NavHostController,
    currentScreen: String,
    onScreenChange: (String) -> Unit,
    isUserLoggedIn: Boolean,
    levels: List<Level>,
    images: List<MediaItem>,
    videos: List<MediaItem>
) {
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") { SplashScreen(navController) }
        composable("auth") { AuthScreen(navController) }
        composable("profile") {
            ProfileScreen(
                currentScreen = currentScreen,
                onScreenChange = onScreenChange
            )
        }
        composable("levels") {
            LevelsScreen(
                currentScreen = currentScreen,
                onScreenChange = onScreenChange,
                currentLevel = 1,
                currentScore = 100,
                levels = levels,
                onLevelStart = {}
            )
        }
        composable("library") {
            LibraryScreen(
                currentScreen = currentScreen,
                onScreenChange = onScreenChange,
                imageItems = images,
                videoItems = videos
            )
        }
    }
}

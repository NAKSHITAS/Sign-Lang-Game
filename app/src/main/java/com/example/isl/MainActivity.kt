package com.example.isl


import android.content.Context
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.navigation.compose.*
import com.example.isl.ui.*
import com.example.isl.ui.theme.ISLTheme
import com.example.isl.nav.NavGraph
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import com.example.isl.data.MediaItem
import com.example.isl.data.Level
import com.google.firebase.auth.FirebaseAuth
import android.util.Base64
import android.util.Log
import java.security.MessageDigest
import android.content.pm.PackageManager
import android.os.Build

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        printAppSignatures(this) // 🔐 Prints SHA1 to Logcat

        enableEdgeToEdge()
        setContent {
            ISLTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ISLApp()
                }
            }
        }
    }

}

@Composable
fun ISLApp() {
    val navController = rememberNavController()
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    var currentScreen by remember { mutableStateOf("splash") }

    // Firebase Auth check
    val isUserLoggedIn = remember {
        FirebaseAuth.getInstance().currentUser != null
    }

    // Dummy content
    val dummyLevels = listOf(
        Level(levelId = 1, title = "Alphabets", description = "Learn A-Z letters", isUnlocked = true),
        Level(levelId = 2, title = "Words", description = "Learn common words", isUnlocked = false),
    )
    val dummyImages = listOf<MediaItem>()
    val dummyVideos = listOf<MediaItem>()

    fun navigateSafely(route: String) {
        if (route != currentScreen) {
            currentScreen = route
            navController.navigate(route) {
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    NavGraph(
        navController = navController,
        currentScreen = currentScreen,
        onScreenChange =  { route -> navigateSafely(route) },
        isUserLoggedIn = isUserLoggedIn,
        levels = dummyLevels,
        images = dummyImages,
        videos = dummyVideos
    )
}
@Suppress("DEPRECATION")
fun printAppSignatures(context: Context) {
    try {
        val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_SIGNING_CERTIFICATES
            ).signingInfo?.apkContentsSigners
        } else {
            context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_SIGNATURES
            ).signatures
        }

        signatures?.forEach { signature ->
            val sha1Digest = MessageDigest.getInstance("SHA-1")
            val sha256Digest = MessageDigest.getInstance("SHA-256")

            sha1Digest.update(signature.toByteArray())
            sha256Digest.update(signature.toByteArray())

            val sha1Base64 = Base64.encodeToString(sha1Digest.digest(), Base64.NO_WRAP)
            val sha256Hex = sha256Digest.digest().joinToString(":") {
                String.format("%02X", it)
            }

            Log.e("APP_SIGNATURE", "🔑 SHA-1  (Base64): $sha1Base64")
            Log.e("APP_SIGNATURE", "🛡️ SHA-256 (Hex): $sha256Hex")
        }
    } catch (e: Exception) {
        Log.e("APP_SIGNATURE", "Failed to get app signature", e)
    }
}



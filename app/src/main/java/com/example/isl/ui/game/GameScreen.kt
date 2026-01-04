package com.example.isl.ui.game

import com.example.isl.viewmodel.GameViewModel
import android.Manifest
import android.content.pm.PackageManager
import android.widget.FrameLayout
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.isl.camera.CameraXManager
import com.example.isl.ml.TFLiteGestureClassifier
import kotlinx.coroutines.flow.collectLatest

/**
 * A Compose Game screen showing how to host PreviewView and connect CameraXManager -> GameViewModel.
 *
 * Usage:
 * - Provide a CameraXManager created in Activity or remember with application Context and classifier.
 * - This composable requests camera permission and binds/unbinds the camera lifecycle.
 *
 * NOTE: The heavy logic (CameraXManager, classifier) lives outside Compose.
 */
@Composable
fun GameScreen(
    lifecycleOwner: LifecycleOwner,
    cameraManager: CameraXManager,
    viewModel: GameViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Camera permission launcher
    val hasCameraPermission = remember {
        ContextCompat.checkSelfPermission(
            lifecycleOwner as? android.content.Context ?: throw IllegalStateException("Invalid context"),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
        onResult = {}
    )

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { ctx ->
            PreviewView(ctx).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            }
        }, update = { previewView ->
            // Bind camera when we have permission and this composable is active
            if (ContextCompat.checkSelfPermission(previewView.context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
            ) {
                cameraManager.bindToLifecycle(lifecycleOwner, previewView)
            }
        })

        // Collect detection events and forward to ViewModel
        LaunchedEffect(cameraManager) {
            cameraManager.detectionEvents.collectLatest { result ->
                viewModel.onGestureDetected(result)
            }
        }

        // TODO: UI overlays showing score, expected gesture, last result, feedback, etc., using uiState
    }
}
package com.example.isl.ml

import android.graphics.Bitmap
import com.example.isl.model.GestureResult

/**
 * Interface for an on-device gesture classifier.
 * Implementations must be thread-safe and run off the main thread.
 */
interface GestureClassifier {
    suspend fun classify(bitmap: Bitmap): GestureResult
    fun close()
}
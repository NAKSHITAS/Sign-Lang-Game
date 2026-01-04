package com.example.isl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.isl.data.FeedbackMessage
import com.example.isl.data.FeedbackType
import com.example.isl.data.Sign
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {

    // List of signs (letters/words) to be shown in this game session
    private val _gameSigns = MutableStateFlow<List<Sign>>(emptyList())
    val gameSigns: StateFlow<List<Sign>> = _gameSigns.asStateFlow()

    // Index of the currently visible sign
    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    // Dynamically fetches the current sign based on the index and sign list
    val currentSign: StateFlow<Sign?> = combine(_gameSigns, _currentIndex) { list, index ->
        list.getOrNull(index)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    // Tracks the user’s current score
    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score.asStateFlow()

    // Indicates whether the current game session has ended
    private val _isGameOver = MutableStateFlow(false)
    val isGameOver: StateFlow<Boolean> = _isGameOver.asStateFlow()

    // Holds the latest feedback message (used for UI feedback like "Good job!")
    private val _feedbackMessage = MutableStateFlow<FeedbackMessage?>(null)
    val feedbackMessage: StateFlow<FeedbackMessage?> = _feedbackMessage.asStateFlow()

    // Prevents multiple inputs while feedback is being shown
    private val _isEvaluating = MutableStateFlow(false)
    val isEvaluating: StateFlow<Boolean> = _isEvaluating.asStateFlow()

    fun onGestureDetected(detectedLabel: String, confidence: Float) {
        // Do nothing if we're already evaluating feedback
        if (_isEvaluating.value) return

        val current = currentSign.value ?: return
        val expected = current.symbol // your Sign has `symbol` (e.g., "A")

        // Simple exact-match + confidence rule:
        val match = detectedLabel.equals(expected, ignoreCase = true)
        // If you prefer using confidence threshold instead, call evaluateWithConfidence().
        // Here we'll treat strong match (label equals expected && confidence >= 0.8) as success.
        val confidenceThreshold = 0.8f

        if (match && confidence >= confidenceThreshold) {
            evaluateWithMLResult(true)
        } else {
            // Option: if label matches but confidence is lower, use evaluateWithConfidence to show partial feedback
            evaluateWithConfidence(confidence, threshold = confidenceThreshold)
        }
    }
    /**
     * Initializes a new game session with the provided list of signs.
     * Resets score, index, and state flags.
     */
    fun startGame(signs: List<Sign>) {
        _gameSigns.value = signs
        _currentIndex.value = 0
        _score.value = 0
        _isGameOver.value = false
        _feedbackMessage.value = null
        _isEvaluating.value = false
    }

    /**
     * Accepts ML evaluation (true if sign matched correctly),
     * then shows corresponding feedback and proceeds to next sign after delay.
     */
    fun evaluateWithMLResult(isCorrect: Boolean) {
        _isEvaluating.value = true

        val feedback = if (isCorrect) {
            _score.value += 10
            FeedbackMessage(
                message = "Good job! ✅",
                type = FeedbackType.SUCCESS,
                iconRes = null,
                colorHex = "#4CAF50", // Green for correct
                playSound = true
            )
        } else {
            FeedbackMessage(
                message = "Well tried! ❌",
                type = FeedbackType.ERROR,
                iconRes = null,
                colorHex = "#F44336", // Red for incorrect
                playSound = false
            )
        }

        _feedbackMessage.value = feedback

        viewModelScope.launch {
            delay(1500) // Allow time for feedback to be shown
            _feedbackMessage.value = null
            _isEvaluating.value = false
            moveToNextSign()
        }
    }

    /**
     * Optional alternate method: evaluates based on ML confidence score.
     * Uses a threshold to determine feedback level.
     */
    fun evaluateWithConfidence(confidence: Float, threshold: Float = 0.8f) {
        _isEvaluating.value = true

        val feedback = when {
            confidence >= threshold -> {
                _score.value += 10
                FeedbackMessage(
                    message = "Perfect! 🔥",
                    type = FeedbackType.SUCCESS,
                    colorHex = "#4CAF50",
                    playSound = true
                )
            }
            confidence >= 0.5f -> {
                FeedbackMessage(
                    message = "Almost there 👀",
                    type = FeedbackType.INFO,
                    colorHex = "#FFC107",
                    playSound = false
                )
            }
            else -> {
                FeedbackMessage(
                    message = "Well tried! ❌",
                    type = FeedbackType.ERROR,
                    colorHex = "#F44336",
                    playSound = false
                )
            }
        }

        _feedbackMessage.value = feedback

        viewModelScope.launch {
            delay(1500)
            _feedbackMessage.value = null
            _isEvaluating.value = false
            moveToNextSign()
        }
    }

    /**
     * Moves to the next sign in the sequence.
     * If no more signs are left, marks the game as over.
     */
    private fun moveToNextSign() {
        if (_currentIndex.value < _gameSigns.value.size - 1) {
            _currentIndex.value++
        } else {
            _isGameOver.value = true
        }
    }

    /**
     * Resets the current game session while keeping the same sign list.
     * Useful for retrying the level or game.
     */
    fun resetGame() {
        _currentIndex.value = 0
        _score.value = 0
        _isGameOver.value = false
        _feedbackMessage.value = null
        _isEvaluating.value = false
    }
}

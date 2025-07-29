package com.example.isl.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.isl.data.Level
import com.example.isl.data.Reward
import com.example.isl.data.Sign
import com.example.isl.data.SignMedia
import com.example.isl.data.User
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    //importing ui state or data class
    private val _uiState = MutableStateFlow(User())
    val uiState: StateFlow<User> = _uiState.asStateFlow()

    // All levels
    private val _levels = MutableStateFlow<List<Level>>(emptyList())
    val levels: StateFlow<List<Level>> = _levels

    // Current level
    val currentLevel: StateFlow<Level?> = MutableStateFlow(null)

    // All sign media mapped by sign ID
    //empty map: each sign id mapped to SignMedia. rn, blank.
    private val _signMediaMap = MutableStateFlow<Map<String, SignMedia>>(emptyMap())
    val signMediaMap: StateFlow<Map<String, SignMedia>> = _signMediaMap

    // Rewards
    private val _rewards = MutableStateFlow<List<Reward>>(emptyList())
    val rewards: StateFlow<List<Reward>> = _rewards

    init {
        loadInitialLevels()
        loadSignMedia()
        loadRewards()
        if (auth.currentUser != null) {
            loadUserFromFirestore()
        }
    }

    private fun loadInitialLevels() {
        viewModelScope.launch {
            val demoLevels = listOf(
                Level(
                    levelId = 1,
                    title = "Basic Signs",
                    description = "Start with simple signs",
                    signs = listOf(
                        Sign("1", "A"),
                        Sign("2", "B"),
                        Sign("3", "C")
                    )
                ),
                Level(
                    levelId = 2,
                    title = "Daily Words",
                    description = "Learn common words",
                    signs = listOf(
                        Sign("4", "Hello"),
                        Sign("5", "Thank You")
                    )
                )
            )
            _levels.value = demoLevels
            //first level as the current level
            updateCurrentLevel()
        }
    }

    //load and map media, associate by signid
    private fun loadSignMedia() {
        viewModelScope.launch {
            val media = listOf(
                SignMedia("1", "url_to_A_image", "url_to_A_video", "Sign A"),
                SignMedia("2", "url_to_B_image", "url_to_B_video", "Sign B"),
                SignMedia("3", "url_to_C_image", "url_to_C_video", "Sign C"),
                SignMedia("4", "url_to_Hello_image", "url_to_Hello_video", "Sign Hello"),
                SignMedia("5", "url_to_ThankYou_image", "url_to_ThankYou_video", "Sign Thank You")
            )
            _signMediaMap.value = media.associateBy { it.signId }
        }
    }

    private fun loadRewards() {
        _rewards.value = listOf(
            Reward("1", "First Sign!", "You completed your first sign!"),
            Reward("2", "Level 1 Champ", "You completed all signs in Level 1")
        )
    }

    private var currentLevelIndex = 0

    private fun updateCurrentLevel() {
        val currentLevelId = _uiState.value.currentLevel
        val levelExists = _levels.value.any { it.levelId == currentLevelId }
        //Validates that the current level still exists in the level list. If not, ends the game
        if (!levelExists) {
            _uiState.update { it.copy(isGameOver = true) }
        }
    }

    // .update safely modify immutable data classes in a reactive flow.
    fun setUser(name: String, id: String) {
        _uiState.update { it.copy(name = name, userId = id) }
        updateCurrentLevel()
    }

    fun completeSign(signId: String) {
        _uiState.update {
            if (signId !in it.completedSigns) //only add the sign if not in completed
                it.copy(completedSigns = it.completedSigns + signId) //adds the new sign ID
            else it
        }
        checkRewards()
        checkLevelCompletion()
    }

    private fun checkLevelCompletion() {
        //find current level , return if level dne
        val level = _levels.value.find { it.levelId == _uiState.value.currentLevel } ?: return
        val userCompletedSigns = _uiState.value.completedSigns
        val allDone = level.signs.all { userCompletedSigns.contains(it.id) }

        if (allDone) {
            moveToNextLevel()
        }
    }

    private fun moveToNextLevel() {
        val nextLevelId = _uiState.value.currentLevel + 1
        val levelExists = _levels.value.any { it.levelId == nextLevelId }
        // if level exists in levels then update current level with next
        if (levelExists) {
            _uiState.update {
                it.copy(
                    currentLevel = nextLevelId,
                    isLevelCompleted = false
                )
            }
            updateCurrentLevel()
        } else {
            _uiState.update { it.copy(isGameOver = true) }
        }
    }

    private fun checkRewards() {
        val updatedRewards = _rewards.value.map { reward ->
            when (reward.rewardId) {
                "1" -> reward.copy(unlocked = _uiState.value.completedSigns.isNotEmpty())
                "2" -> {
                    val level = _levels.value.find { it.levelId == 1 }
                    val isDone = level?.signs?.all { _uiState.value.completedSigns.contains(it.id) } == true
                    reward.copy(unlocked = isDone)
                }
                else -> reward
            }
        }
        _rewards.value = updatedRewards
    }

    fun resetProgress() {
        _uiState.update {
            it.copy(
                currentLevel = 1,
                completedSigns = emptyList(),
                isLevelCompleted = false,
                isGameOver = false
            )
        }
        updateCurrentLevel()
        loadRewards()
    }
    // FOR OPEN PRACTICE MODE


    // The sign selected by the user from the sign list
    private val _selectedSign = MutableStateFlow<Sign?>(null)
    val selectedSign: StateFlow<Sign?> = _selectedSign.asStateFlow()

    // The SignMedia (image, video, description) for the selected sign
    // Reactively updates if user changes the selected sign or media is loaded
    val selectedSignMedia: StateFlow<SignMedia?> = combine(_selectedSign, _signMediaMap) { sign, mediaMap ->
        sign?.let { mediaMap[it.id] }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    // Call this when user taps on a sign from the list to begin learning
    fun selectSign(sign: Sign) {
        _selectedSign.value = sign
    }

    fun loadUserFromFirestore() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(User::class.java)
                    user?.let {
                        _uiState.value = it
                        updateCurrentLevel()
                    }
                }
            }
            .addOnFailureListener {
                // Log or handle the error
            }
    }

}

/*🧠 WHY Do We Write It Twice?
🔒 1. Encapsulation & Immutability
MutableStateFlow lets you modify the value: .value = newValue

But we don’t want the UI or other classes changing state.

So we expose only the read-only version: StateFlow.

This protects state from accidental or unsafe mutation outside the ViewModel.

Analogy:
It's like keeping the remote control in your hand (private _channel)
but letting others see the channel on the screen (public channel).

Only you can press buttons.

📦 2. Clean MVVM Separation
ViewModel holds the logic (_someFlow)

UI observes someFlow and reacts only

This follows unidirectional data flow — updates go ↓ from ViewModel → UI

*/
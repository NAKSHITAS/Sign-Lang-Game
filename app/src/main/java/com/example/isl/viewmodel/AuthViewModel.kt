package com.example.isl.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.isl.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// Sealed class to handle auth states
sealed class AuthResult {
    object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
    object Loading : AuthResult()
    object Idle : AuthResult()
}

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    private val _authState = mutableStateOf<AuthResult>(AuthResult.Idle)
    val authState: State<AuthResult> = _authState

    private var cachedName: String = "" // Store the name temporarily

    fun signUp(name: String, email: String, password: String) {
        cachedName = name // Save it for use after signup

        _authState.value = AuthResult.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    saveUserInfoToFirestore(name, email)
                    _authState.value = AuthResult.Success
                } else {
                    _authState.value = AuthResult.Error(task.exception?.message ?: "Sign up failed")
                }
            }
    }

    private fun saveUserInfoToFirestore(name: String, email: String) {
        val userId = auth.currentUser?.uid ?: return
        val user = User(
            userId = userId,
            name = name,
            email = email
        )
        usersCollection.document(userId).set(user)
    }

    fun signIn(email: String, password: String) {
        _authState.value = AuthResult.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _authState.value = if (task.isSuccessful) {
                    AuthResult.Success
                } else {
                    AuthResult.Error(task.exception?.message ?: "Sign in failed")
                }
            }
    }

    fun resetAuthState() {
        _authState.value = AuthResult.Idle
    }
}

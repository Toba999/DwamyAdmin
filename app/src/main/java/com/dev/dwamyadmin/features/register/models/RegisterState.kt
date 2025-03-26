package com.dev.dwamyadmin.features.register.models

sealed class RegisterState {
    object Idle : RegisterState()  // Initial state
    object Loading : RegisterState()  // When registering
    data class Success(val message: String) : RegisterState()  // On success
    data class Failure(val error: String) : RegisterState()  // On failure
}
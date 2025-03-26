package com.dev.dwamyadmin.domain.models

data class Admin(
    val id: String = "",  // Firestore ID
    val name: String = "",
    val email: String = "",
    val password: String = ""  // Ensure secure storage in production!
)
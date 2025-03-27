package com.dev.dwamyadmin.domain.models

data class Employee(
    val id: String = "",  // Firestore ID
    val adminId: String = "",  // Links employee to their admin
    val name: String = "",
    val email: String = "",
    val password: String = "",  // Ensure secure storage in production!
    val workDays: String = "",
    val latitude : Double = 0.0,
    val longitude : Double = 0.0,
    val address : String = "",
    val profession : String = "",
    val startTime: Int = 9,  // Work start time (24-hour format)
    val endTime: Int = 17   // Work end time (24-hour format)
)

package com.dev.dwamyadmin.domain.models

import com.google.firebase.Timestamp

data class EmployeeAttendence (
    val id: String,
    val name: String,
    val profession: String,
    val checkIn: Timestamp,
    val checkOut: Timestamp
)
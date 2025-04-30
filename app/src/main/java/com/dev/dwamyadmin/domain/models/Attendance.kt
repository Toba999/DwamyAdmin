package com.dev.dwamyadmin.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Attendance(
    val id: String = "",
    val employeeId: String = "",
    val adminId: String = "",
    val employeeName: String = "",
    val profession: String = "",
    val checkInTime: String = "",
    val checkOutTime: String = "",
    val checkInLat: Double = 0.0,
    val checkInLng: Double = 0.0,
    val checkOutLat: Double = 0.0,
    val checkOutLng: Double = 0.0,
    val date: String = "",
    val startTime : String = "",
    val endTime : String = ""
) : Parcelable


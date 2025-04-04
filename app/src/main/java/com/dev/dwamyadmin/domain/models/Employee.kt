package com.dev.dwamyadmin.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Employee(
    val id: String? = null,
    val adminId: String = "",
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val workDays: String = "",
    val latitude : Double = 0.0,
    val longitude : Double = 0.0,
    val address : String = "",
    val imageUri : String = "",
    val area : Int = 100,
    val profession : String = "",
    val startTime: Int = 9,
    val endTime: Int = 17,
    val deviceId : String? = null
) : Parcelable

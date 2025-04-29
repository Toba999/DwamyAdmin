package com.dev.dwamyadmin.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class AttendanceStats(
    val employeeName: String = "",
    val profession: String = "",
    val percentageTime: String = "",
    val percentage: String = ""
) : Parcelable


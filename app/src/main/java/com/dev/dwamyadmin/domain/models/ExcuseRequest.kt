package com.dev.dwamyadmin.domain.models

import com.dev.dwamyadmin.features.excuse.presentation.view.ExcuseItem
import java.text.SimpleDateFormat
import java.util.Locale

data class ExcuseRequest(
    val id: String = "",
    val employeeId: String,
    val employeeName: String,
    val adminId: String,
    val issuedDate: String,
    val excuseDate: Long,
    val excuseType: ExcuseType,
    val excuseReason: String,
    val excuseTime: String,       // Time of late check-in or early check-out (e.g., "10:30 AM" or "3:00 PM")
    val status: ExcuseStatus = ExcuseStatus.PENDING
)

enum class ExcuseType {
    LATE, EARLY_LEAVE
}

enum class ExcuseStatus {
    PENDING, ACCEPTED, REJECTED
}



fun ExcuseRequest.toExcuseItem(): ExcuseItem {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val formattedDate = dateFormat.format(excuseDate) // Convert timestamp to date

    return ExcuseItem(
        id = id,
        timestamp = formattedDate, // Excuse date as a formatted string
        title = if (excuseType == ExcuseType.LATE) "تأخير" else "مغادرة مبكرة",
        fromTime = if (excuseType == ExcuseType.LATE) excuseTime else "-",
        toTime = if (excuseType == ExcuseType.EARLY_LEAVE) excuseTime else "-",
        applicantName = employeeName,
        status = when (status) {
            ExcuseStatus.PENDING -> "قيد الانتظار"
            ExcuseStatus.ACCEPTED -> "مقبول"
            ExcuseStatus.REJECTED -> "مرفوض"
        }
    )
}

package com.dev.dwamyadmin.domain.models

import com.dev.dwamyadmin.features.vacation.presentation.view.VacationItem
import com.dev.dwamyadmin.utils.DateAndTimePicker.toDayOfWeek
import com.dev.dwamyadmin.utils.DateAndTimePicker.toFormattedDate


data class LeaveRequest(
    val id: String = "",
    val employeeId: String,
    val employeeName: String,
    val adminId: String,
    val issuedDate: String,
    val dayFrom: String,
    val dayTo: String,
    val status: LeaveStatus = LeaveStatus.PENDING
)

enum class LeaveStatus {
    PENDING, ACCEPTED, REJECTED
}

fun LeaveRequest.toVacationItem(): VacationItem {
    return VacationItem(
        id = id,
        timestamp = issuedDate.toLong().toFormattedDate() + " - " + issuedDate.toLong().toDayOfWeek(),
        title = "طلب إجازة",
        fromTime = dayFrom.toLong().toFormattedDate() + " - " + dayFrom.toLong().toDayOfWeek(),
        toTime = dayTo.toLong().toFormattedDate() + " - " + dayTo.toLong().toDayOfWeek(),
        applicantName = employeeName,
        status = when (status) {
            LeaveStatus.PENDING -> "قيد الانتظار"
            LeaveStatus.ACCEPTED -> "مقبولة"
            LeaveStatus.REJECTED -> "مرفوضة"
        }
    )
}


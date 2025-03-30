package com.dev.dwamyadmin.domain.models



data class LeaveRequest(
    val id: String = "",
    val employeeId: String = "",
    val employeeName: String = "",
    val adminId: String = "",
    val issuedDate: String = "",
    val requestType: String = "",
    val dayFrom: String = "",
    val reason: String = "",
    val dayTo: String = "",
    var status: LeaveStatus = LeaveStatus.PENDING
)

enum class LeaveStatus {
    PENDING, ACCEPTED, REJECTED
}

fun LeaveStatus.toArabic(): String {
    return when (this) {
        LeaveStatus.PENDING -> "قيد الانتظار"
        LeaveStatus.ACCEPTED -> "مقبولة"
        LeaveStatus.REJECTED -> "مرفوضة"
    }
}
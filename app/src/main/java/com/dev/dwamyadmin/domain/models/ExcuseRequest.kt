package com.dev.dwamyadmin.domain.models



data class ExcuseRequest(
    val id: String = "",
    val employeeId: String = "",
    val employeeName: String = "",
    val adminId: String = "",
    val issuedDate: String = "",
    val excuseDateFrom: String = "",
    val excuseDateTo: String = "",
    val excuseDate : String = "",
    val excuseType: ExcuseType = ExcuseType.LATE,
    val excuseReason: String = "",
    var status: ExcuseStatus = ExcuseStatus.PENDING
)

enum class ExcuseType {
    LATE, EARLY_LEAVE
}

enum class ExcuseStatus {
    PENDING, ACCEPTED, REJECTED
}


package com.dev.dwamyadmin.domain.repo

import android.net.Uri
import com.dev.dwamyadmin.domain.models.Admin
import com.dev.dwamyadmin.domain.models.Attendance
import com.dev.dwamyadmin.domain.models.Employee
import com.dev.dwamyadmin.domain.models.EmployeeAttendence
import com.dev.dwamyadmin.domain.models.ExcuseRequest
import com.dev.dwamyadmin.domain.models.ExcuseStatus
import com.dev.dwamyadmin.domain.models.LeaveRequest
import com.dev.dwamyadmin.domain.models.LeaveStatus
import java.util.Date

interface FireBaseRepo {
    suspend fun isEmailTaken(email: String): Boolean
    suspend fun registerAdmin(admin: Admin): Boolean
    suspend fun registerEmployee(employee: Employee): Boolean
    suspend fun loginAdmin(email: String, password: String): Boolean
    suspend fun getLeaveRequestsByAdmin(adminId: String): List<LeaveRequest>
    suspend fun getExcuseRequestsByAdmin(adminId: String): List<ExcuseRequest>
    suspend fun updateLeaveRequestStatus(requestId: String, status: LeaveStatus): Boolean
    suspend fun updateExcuseRequestStatus(requestId: String, status: ExcuseStatus): Boolean
    suspend fun getEmployeesByAdmin(adminId: String): List<Employee>
    suspend fun getEmployeesByDate(date: String,adminId : String): List<Attendance>
    suspend fun deleteEmployee(employeeId: String): Boolean
    suspend fun uploadImage(imageUri: Uri): String?
    suspend fun getAttendanceCountPerEmployeePerMonth(adminId: String): Map<Employee, Map<String, Int>>
}
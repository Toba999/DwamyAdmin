package com.dev.dwamyadmin.domain.repo

import com.dev.dwamyadmin.domain.models.Admin
import com.dev.dwamyadmin.domain.models.Employee

interface FireBaseRepo {
    suspend fun isEmailTaken(email: String): Boolean
    suspend fun registerAdmin(admin: Admin): Boolean
    suspend fun registerEmployee(employee: Employee): Boolean
    suspend fun loginAdmin(email: String, password: String): Boolean
}
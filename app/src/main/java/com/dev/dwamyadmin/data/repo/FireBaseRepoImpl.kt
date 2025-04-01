package com.dev.dwamyadmin.data.repo

import android.net.Uri
import com.dev.dwamyadmin.domain.models.Admin
import com.dev.dwamyadmin.domain.models.Employee
import com.dev.dwamyadmin.domain.models.EmployeeAttendence
import com.dev.dwamyadmin.domain.models.ExcuseRequest
import com.dev.dwamyadmin.domain.models.ExcuseStatus
import com.dev.dwamyadmin.domain.models.LeaveRequest
import com.dev.dwamyadmin.domain.models.LeaveStatus
import com.dev.dwamyadmin.domain.repo.FireBaseRepo
import com.dev.dwamyadmin.utils.SharedPrefManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class FireBaseRepoImpl @Inject constructor(
    firestore: FirebaseFirestore,
    storage: FirebaseStorage,
    private val sharedPrefManager: SharedPrefManager
) : FireBaseRepo {
    private val storageReference = storage.reference.child("employee_images")

    private val leaveRequestsCollection = firestore.collection("leave_requests")
    private val excuseRequestsCollection = firestore.collection("excuse_requests")

    private val adminsCollection = firestore.collection("admins")
    private val employeesCollection = firestore.collection("employees")

    override suspend fun isEmailTaken(email: String): Boolean {
        return try {
            val adminQuery = adminsCollection.whereEqualTo("email", email).get().await()
            val employeeQuery = employeesCollection.whereEqualTo("email", email).get().await()
            !adminQuery.isEmpty || !employeeQuery.isEmpty
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun registerAdmin(admin: Admin): Boolean {
        return try {
            if (isEmailTaken(admin.email)) {
                throw IllegalArgumentException("Email is already in use")
            }
            val documentRef = adminsCollection.document()
            val adminWithId = admin.copy(id = documentRef.id)
            documentRef.set(adminWithId).await()
            sharedPrefManager.setAdminData(adminWithId.id, adminWithId.name, adminWithId.email)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun registerEmployee(employee: Employee): Boolean {
        return try {
            if (isEmailTaken(employee.email)) {
                throw IllegalArgumentException("Email is already in use")
            }
            val documentRef = employeesCollection.document()
            val employeeWithId = employee.copy(id = documentRef.id)
            documentRef.set(employeeWithId).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun loginAdmin(email: String, password: String): Boolean {
        return try {
            val querySnapshot = adminsCollection.whereEqualTo("email", email).get().await()
            if (!querySnapshot.isEmpty) {
                val admin = querySnapshot.documents[0].toObject(Admin::class.java)
                if (admin != null && admin.password == password) {
                    sharedPrefManager.setAdminData(admin.id, admin.name, admin.email)
                    return true
                }
            }
            false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    override suspend fun getLeaveRequestsByAdmin(adminId: String): List<LeaveRequest> {
        return try {
            val querySnapshot = leaveRequestsCollection.whereEqualTo("adminId", adminId).get().await()
            querySnapshot.documents.mapNotNull { it.toObject(LeaveRequest::class.java) }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getExcuseRequestsByAdmin(adminId: String): List<ExcuseRequest> {
        return try {
            val querySnapshot = excuseRequestsCollection.whereEqualTo("adminId", adminId).get().await()
            querySnapshot.documents.mapNotNull { it.toObject(ExcuseRequest::class.java) }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun updateLeaveRequestStatus(requestId: String, status: LeaveStatus): Boolean {
        return try {
            val querySnapshot = leaveRequestsCollection.whereEqualTo("id", requestId).get().await()
            if (!querySnapshot.isEmpty) {
                val document = querySnapshot.documents.first()
                document.reference.update("status", status.name).await()
                true
            } else {
                println("No leave request found with ID: $requestId")
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun updateExcuseRequestStatus(requestId: String, status: ExcuseStatus): Boolean {
        return try {
            val querySnapshot = excuseRequestsCollection.whereEqualTo("id", requestId).get().await()
            if (!querySnapshot.isEmpty) {
                val document = querySnapshot.documents.first()
                document.reference.update("status", status.name).await()
                true
            } else {
                println("No leave request found with ID: $requestId")
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    override suspend fun getEmployeesByAdmin(adminId: String): List<Employee> {
        return try {
            val querySnapshot = employeesCollection.whereEqualTo("adminId", adminId).get().await()
            querySnapshot.documents.mapNotNull { it.toObject(Employee::class.java) }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getEmployeesByDate(date: Date): List<EmployeeAttendence> {
        TODO("Not yet implemented")

    }

    override suspend fun deleteEmployee(employeeId: String): Boolean {
        return try {
            employeesCollection.document(employeeId).delete().await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun uploadImage(imageUri: Uri): String? {
        return try {
            val fileRef = storageReference.child("${System.currentTimeMillis()}.jpg")
            fileRef.putFile(imageUri).await()
            fileRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

package com.dev.dwamyadmin.data.repo

import android.net.Uri
import com.dev.dwamyadmin.domain.models.Admin
import com.dev.dwamyadmin.domain.models.Attendance
import com.dev.dwamyadmin.domain.models.Employee
import com.dev.dwamyadmin.domain.models.ExcuseRequest
import com.dev.dwamyadmin.domain.models.ExcuseStatus
import com.dev.dwamyadmin.domain.models.LeaveRequest
import com.dev.dwamyadmin.domain.models.LeaveStatus
import com.dev.dwamyadmin.domain.repo.FireBaseRepo
import com.dev.dwamyadmin.utils.FireStoreConstant.ADMINS_COLLECTION
import com.dev.dwamyadmin.utils.FireStoreConstant.ADMIN_ID
import com.dev.dwamyadmin.utils.FireStoreConstant.ATTENDANCE_COLLECTION
import com.dev.dwamyadmin.utils.FireStoreConstant.ATTENDANCE_ID
import com.dev.dwamyadmin.utils.FireStoreConstant.ATTENDANCE_STATUS
import com.dev.dwamyadmin.utils.FireStoreConstant.DATE
import com.dev.dwamyadmin.utils.FireStoreConstant.EMPLOYEES_COLLECTION
import com.dev.dwamyadmin.utils.FireStoreConstant.EMPLOYEE_EMAIL
import com.dev.dwamyadmin.utils.FireStoreConstant.EMPLOYEE_ID
import com.dev.dwamyadmin.utils.FireStoreConstant.EMPLOYEE_IMAGES_PATH
import com.dev.dwamyadmin.utils.FireStoreConstant.EXCUSE_REQUESTS_COLLECTION
import com.dev.dwamyadmin.utils.FireStoreConstant.LEAVE_REQUESTS_COLLECTION
import com.dev.dwamyadmin.utils.SharedPrefManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FireBaseRepoImpl @Inject constructor(
    firestore: FirebaseFirestore,
    storage: FirebaseStorage,
    private val sharedPrefManager: SharedPrefManager
) : FireBaseRepo {
    private val storageReference = storage.reference.child(EMPLOYEE_IMAGES_PATH)
    private val leaveRequestsCollection = firestore.collection(LEAVE_REQUESTS_COLLECTION)
    private val excuseRequestsCollection = firestore.collection(EXCUSE_REQUESTS_COLLECTION)
    private val employeesCollection = firestore.collection(EMPLOYEES_COLLECTION)
    private val attendanceCollection = firestore.collection(ATTENDANCE_COLLECTION)
    private val adminsCollection = firestore.collection(ADMINS_COLLECTION)

    override suspend fun isEmailTaken(email: String): Boolean {
        return try {
            val adminQuery = adminsCollection.whereEqualTo(EMPLOYEE_EMAIL, email).get().await()
            val employeeQuery = employeesCollection.whereEqualTo(EMPLOYEE_EMAIL, email).get().await()
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
            // Check if the email is already taken
            if (isEmailTaken(employee.email) && employee.id == "0") {
                throw IllegalArgumentException("البريد الإلكتروني مستخدم بالفعل")
            }

            // Check if the employee with the same ID already exists
            val existingEmployeeQuery = employeesCollection.whereEqualTo(ATTENDANCE_ID, employee.id).get().await()

            if (existingEmployeeQuery.isEmpty) {
                // If no employee exists with the same ID, create a new one
                val documentRef = employeesCollection.document()
                val employeeWithId = employee.copy(id = documentRef.id)
                documentRef.set(employeeWithId).await()
            } else {
                // If the employee exists, update the existing document with the new data
                val existingEmployeeDoc = existingEmployeeQuery.documents.first()
                existingEmployeeDoc.reference.set(employee).await()
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    override suspend fun loginAdmin(email: String, password: String): Boolean {
        return try {
            val querySnapshot = adminsCollection.whereEqualTo(EMPLOYEE_EMAIL, email).get().await()
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
            val querySnapshot = leaveRequestsCollection.whereEqualTo(ADMIN_ID, adminId).get().await()
            querySnapshot.documents.mapNotNull { it.toObject(LeaveRequest::class.java) }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getExcuseRequestsByAdmin(adminId: String): List<ExcuseRequest> {
        return try {
            val querySnapshot = excuseRequestsCollection.whereEqualTo(ADMIN_ID, adminId).get().await()
            querySnapshot.documents.mapNotNull { it.toObject(ExcuseRequest::class.java) }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun updateLeaveRequestStatus(requestId: String, status: LeaveStatus): Boolean {
        return try {
            val querySnapshot = leaveRequestsCollection.whereEqualTo(ATTENDANCE_ID, requestId).get().await()
            if (!querySnapshot.isEmpty) {
                val document = querySnapshot.documents.first()
                document.reference.update(ATTENDANCE_STATUS, status.name).await()
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
            val querySnapshot = excuseRequestsCollection.whereEqualTo(ATTENDANCE_ID, requestId).get().await()
            if (!querySnapshot.isEmpty) {
                val document = querySnapshot.documents.first()
                document.reference.update(ATTENDANCE_STATUS, status.name).await()
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
            val querySnapshot = employeesCollection.whereEqualTo(ADMIN_ID, adminId).get().await()
            querySnapshot.documents.mapNotNull { it.toObject(Employee::class.java) }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getEmployeesByDate(date: String, adminId: String): List<Attendance> {
        return try {
            val querySnapshot =
                attendanceCollection.whereEqualTo(ADMIN_ID, adminId)
                    .whereEqualTo(DATE, date)
                    .get()
                    .await()
            querySnapshot.documents.mapNotNull { it.toObject(Attendance::class.java) }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun deleteEmployee(employeeId: String): Boolean {
        return try {
            val leaveRequests = leaveRequestsCollection.whereEqualTo(EMPLOYEE_ID, employeeId).get().await()
            for (document in leaveRequests.documents) {
                document.reference.delete().await()
            }

            val excuseRequests = excuseRequestsCollection.whereEqualTo(EMPLOYEE_ID, employeeId).get().await()
            for (document in excuseRequests.documents) {
                document.reference.delete().await()
            }

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

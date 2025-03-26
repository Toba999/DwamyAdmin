package com.dev.dwamyadmin.data.repo

import com.dev.dwamyadmin.domain.models.Admin
import com.dev.dwamyadmin.domain.models.Employee
import com.dev.dwamyadmin.domain.repo.FireBaseRepo
import com.dev.dwamyadmin.utils.SharedPrefManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FireBaseRepoImpl @Inject constructor(
    firestore: FirebaseFirestore,
    private val sharedPrefManager: SharedPrefManager
) : FireBaseRepo {

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
                    // Save logged-in admin details
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
}

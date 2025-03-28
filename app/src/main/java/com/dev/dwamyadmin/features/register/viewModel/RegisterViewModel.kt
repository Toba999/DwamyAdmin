package com.dev.dwamyadmin.features.register.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.dwamyadmin.domain.models.Admin
import com.dev.dwamyadmin.domain.models.Employee
import com.dev.dwamyadmin.domain.repo.FireBaseRepo
import com.dev.dwamyadmin.features.register.models.RegisterState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repo: FireBaseRepo
) : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()


    private val _imageUrl = MutableStateFlow<String?>(null)
    val imageUrl: StateFlow<String?> get() = _imageUrl

    fun registerAdmin(name: String, email: String, password: String) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            try {
                val success =
                    repo.registerAdmin(Admin(name = name, email = email, password = password))
                _registerState.value =
                    if (success) RegisterState.Success("تم تسجيل صاحب العمل") else RegisterState.Failure(
                        "فشل التسجيل"
                    )
            } catch (e: Exception) {
                _registerState.value = RegisterState.Failure(e.message ?: "خطأ")
            }
        }
    }

    fun registerEmployee(
        name: String,
        email: String,
        profession : String,
        password: String,
        adminId: String,
        workDays: String,
        startTime: Int,
        endTime: Int,
        imageUri: String,
        address: String,
        latitude: Double,
        longitude: Double,
        area : Int
    ) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            try {
                val success = repo.registerEmployee(
                    Employee(
                        name = name,
                        email = email,
                        profession = profession,
                        password = password,
                        adminId = adminId,
                        workDays = workDays,
                        startTime = startTime,
                        endTime = endTime,
                        latitude = latitude ,
                        longitude = longitude ,
                        address = address,
                        imageUri = imageUri,
                        area = area
                    )
                )
                _registerState.value =
                    if (success) RegisterState.Success("تم تسجيل الموظف") else RegisterState.Failure(
                        "فشل التسجيل"
                    )
            } catch (e: Exception) {
                _registerState.value = RegisterState.Failure(e.message ?: "خطأ")
            }
        }
    }


    fun uploadEmployeeImage(imageUri: Uri) {
        viewModelScope.launch {
            val url = repo.uploadImage(imageUri)
            _imageUrl.value = url
        }
    }
}

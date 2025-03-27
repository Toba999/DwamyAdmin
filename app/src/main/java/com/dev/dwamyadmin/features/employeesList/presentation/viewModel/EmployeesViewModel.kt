package com.dev.dwamyadmin.features.employeesList.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.dwamyadmin.domain.models.Employee
import com.dev.dwamyadmin.domain.repo.FireBaseRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.filter

@HiltViewModel
class EmployeesViewModel @Inject constructor(
    private val firebaseRepo: FireBaseRepo
) : ViewModel() {

    private val _employees = MutableStateFlow<List<Employee>?>(null)
    val employees: StateFlow<List<Employee>?> = _employees.asStateFlow()

    private val _deleteEmployeeResult = MutableStateFlow<Boolean?>(null)
    val deleteEmployeeResult: StateFlow<Boolean?> = _deleteEmployeeResult.asStateFlow()

    fun fetchEmployees(adminId: String) {
        viewModelScope.launch {
            _employees.value = firebaseRepo.getEmployeesByAdmin(adminId)
        }
    }

    fun deleteEmployee(employeeId: String) {
        viewModelScope.launch {
            val result = firebaseRepo.deleteEmployee(employeeId)
            _deleteEmployeeResult.value = result

            if (result) {
                _employees.value = _employees.value?.filter { it.id != employeeId }
            }
        }
    }
}

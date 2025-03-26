package com.dev.dwamyadmin.features.vacation.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.dwamyadmin.domain.models.LeaveRequest
import com.dev.dwamyadmin.domain.models.LeaveStatus
import com.dev.dwamyadmin.domain.repo.FireBaseRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class VacationViewModel @Inject constructor(
    private val fireBaseRepo: FireBaseRepo
) : ViewModel() {

    private val _leaveRequests = MutableStateFlow<List<LeaveRequest>>(emptyList())
    val leaveRequests: StateFlow<List<LeaveRequest>> = _leaveRequests

    private val _updateStatusResult = MutableStateFlow<Boolean?>(null)
    val updateStatusResult: StateFlow<Boolean?> = _updateStatusResult

    fun fetchLeaveRequests(adminId: String) {
        viewModelScope.launch {
            val requests = fireBaseRepo.getLeaveRequestsByAdmin(adminId)
            _leaveRequests.value = requests
        }
    }

    fun updateLeaveRequestStatus(requestId: String, status: LeaveStatus) {
        viewModelScope.launch {
            val success = fireBaseRepo.updateLeaveRequestStatus(requestId, status)
            _updateStatusResult.value = success
            if (success) {
                _leaveRequests.value = _leaveRequests.value.map {
                    if (it.id == requestId) it.copy(status = status) else it
                }
            }
        }
    }
}
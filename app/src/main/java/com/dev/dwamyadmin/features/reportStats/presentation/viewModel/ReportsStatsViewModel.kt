package com.dev.dwamyadmin.features.reportStats.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.dwamyadmin.domain.repo.FireBaseRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportsStatsViewModel @Inject constructor(
    private val firebaseRepo: FireBaseRepo
) : ViewModel() {

    private val _attendanceCountState = MutableStateFlow<AttendanceCountUiState>(AttendanceCountUiState.Idle)
    val attendanceCountState: StateFlow<AttendanceCountUiState> = _attendanceCountState.asStateFlow()


    fun getAttendanceCountPerEmployeePerMonth(adminId: String) {
        viewModelScope.launch {
            _attendanceCountState.value = AttendanceCountUiState.Loading
            try {
                val attendanceCount = firebaseRepo.getAttendanceCountPerEmployeePerMonth(adminId)
                _attendanceCountState.value = if (attendanceCount.isNotEmpty()) {
                    AttendanceCountUiState.Success(attendanceCount)
                } else {
                    AttendanceCountUiState.Empty
                }
            } catch (e: Exception) {
                _attendanceCountState.value = AttendanceCountUiState.Error(e.localizedMessage ?: "حدث خطأ في التحميل")
            }
        }
    }
}
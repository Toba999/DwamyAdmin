package com.dev.dwamyadmin.features.reportStats.presentation.viewModel

import com.dev.dwamyadmin.domain.models.Employee


sealed class AttendanceCountUiState {
    object Idle : AttendanceCountUiState()
    object Loading : AttendanceCountUiState()
    object Empty : AttendanceCountUiState()
    data class Success(val attendanceCount: Map<Employee, Map<String, Int>>) : AttendanceCountUiState()
    data class Error(val message: String) : AttendanceCountUiState()
}

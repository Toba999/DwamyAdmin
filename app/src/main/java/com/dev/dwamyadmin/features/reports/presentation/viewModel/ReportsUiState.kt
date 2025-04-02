package com.dev.dwamyadmin.features.reports.presentation.viewModel

import com.dev.dwamyadmin.domain.models.Attendance

sealed class ReportsUiState {
    object Loading : ReportsUiState()
    data class Success(val attendanceList: List<Attendance>) : ReportsUiState()
    data class Error(val message: String) : ReportsUiState()
    object Empty : ReportsUiState()
    object Idle : ReportsUiState()
}
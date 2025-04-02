package com.dev.dwamyadmin.features.reports.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.dwamyadmin.domain.repo.FireBaseRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val firebaseRepo: FireBaseRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReportsUiState>(ReportsUiState.Idle)
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    fun getEmployeesByDate(date: String, adminId: String) {
        viewModelScope.launch {
            _uiState.value = ReportsUiState.Loading
            try {
                val attendance = firebaseRepo.getEmployeesByDate(date, adminId)
                _uiState.value = if (attendance.isNotEmpty()) {
                    ReportsUiState.Success(attendance)
                } else {
                    ReportsUiState.Empty
                }
            } catch (e: Exception) {
                _uiState.value = ReportsUiState.Error(e.localizedMessage ?: "حدث خطأ في التحميل")
            }
        }
    }
}
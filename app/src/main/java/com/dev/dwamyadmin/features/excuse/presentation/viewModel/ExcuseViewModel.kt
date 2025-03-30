package com.dev.dwamyadmin.features.excuse.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.dwamyadmin.domain.models.ExcuseRequest
import com.dev.dwamyadmin.domain.models.ExcuseStatus
import com.dev.dwamyadmin.domain.repo.FireBaseRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ExcuseViewModel @Inject constructor(
    private val firebaseRepo: FireBaseRepo
) : ViewModel() {

    private val _excuseRequests = MutableStateFlow<List<ExcuseRequest>?>(null)
    val excuseRequests: StateFlow<List<ExcuseRequest>?> = _excuseRequests

    private val _updateStatusResult = MutableStateFlow<Boolean?>(null)
    val updateStatusResult: StateFlow<Boolean?> = _updateStatusResult

    fun getExcuseRequests(adminId: String) {
        viewModelScope.launch {
            val excuses = firebaseRepo.getExcuseRequestsByAdmin(adminId)
            _excuseRequests.value = excuses
        }
    }

    fun updateExcuseStatus(excuseId: String, newStatus: ExcuseStatus) {
        viewModelScope.launch {
            val success = firebaseRepo.updateExcuseRequestStatus(excuseId, newStatus)
            _updateStatusResult.value = success
            if (success) {
                _excuseRequests.value = _excuseRequests.value?.map {
                    if (it.id == excuseId) it.copy(status = newStatus) else it
                }
            }
        }
    }
}
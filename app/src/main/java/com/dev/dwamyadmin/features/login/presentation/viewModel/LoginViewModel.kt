package com.dev.dwamyadmin.features.login.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.dwamyadmin.domain.repo.FireBaseRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val fireBaseRepo: FireBaseRepo
) : ViewModel() {

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> get() = _loginState

    fun loginAdmin(email: String, password: String) {
        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            val isSuccess = fireBaseRepo.loginAdmin(email, password)
            if (isSuccess) {
                _loginState.value = LoginState.Success
            } else {
                _loginState.value = LoginState.Failure("البريد الاكترني او كلمة السر غير صحيحة")
            }
        }
    }

    sealed class LoginState {
        object Loading : LoginState()
        object Success : LoginState()
        data class Failure(val message: String) : LoginState()
    }
}

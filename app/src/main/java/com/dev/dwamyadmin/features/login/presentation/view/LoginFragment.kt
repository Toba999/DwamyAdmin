package com.dev.dwamyadmin.features.login.presentation.view

import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.dev.dwamyadmin.R
import com.dev.dwamyadmin.databinding.FragmentLoginBinding
import com.dev.dwamyadmin.features.login.presentation.viewModel.LoginViewModel
import com.dev.dwamyadmin.utils.SharedPrefManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.Executor
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels()

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var sharedPrefManager: SharedPrefManager  // Inject SharedPreferences manager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkForBiometricLogin()  // Check if biometric login is possible
        binding.fingerprintCard.setOnClickListener {
            showBiometricPrompt()
        }
        setupObservers()
        setupListeners()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun checkForBiometricLogin() {
        val savedAdminId = sharedPrefManager.getAdminId()

        if (!savedAdminId.isNullOrEmpty()) {
            val biometricManager = BiometricManager.from(requireContext())
            if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) ==
                BiometricManager.BIOMETRIC_SUCCESS) {
                showBiometricPrompt()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun showBiometricPrompt() {
        val executor: Executor = ContextCompat.getMainExecutor(requireContext())

        val biometricPrompt = BiometricPrompt(this@LoginFragment, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    navigateToContainer()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    showSnackBar(requireView(), "فشل التحقق من بصمة الإصبع", true)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    showSnackBar(requireView(), "خطأ: $errString", true)
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("تسجيل الدخول ببصمة الإصبع")
            .setSubtitle("استخدم بصمة إصبعك لتسجيل الدخول")
            .setNegativeButtonText("إلغاء")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }


    private fun setupObservers() {
        viewModel.loginState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is LoginViewModel.LoginState.Loading -> {
                    showLoading(true)
                }
                is LoginViewModel.LoginState.Success -> {
                    showLoading(false)
                    navigateToContainer()
                }
                is LoginViewModel.LoginState.Failure -> {
                    showLoading(false)
                    showSnackBar(requireView(), state.message, true)
                }
            }
        }
    }

    private fun setupListeners() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEt.text.toString().trim()
            val password = binding.passwordEt.text.toString().trim()
            if (validateInputs(email, password)) {
                viewModel.loginAdmin(email, password)
            }
        }

        binding.goToRegister.setOnClickListener {
            findNavController().navigate(
                R.id.registerFragment,
                null,
                NavOptions.Builder().setPopUpTo(R.id.LoginFragment, true).setLaunchSingleTop(true).build()
            )
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        return when {
            email.isEmpty() -> {
                showSnackBar(requireView(), "البريد الإلكتروني مطلوب", true)
                false
            }
            password.isEmpty() -> {
                showSnackBar(requireView(), "كلمة المرور مطلوبة", true)
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                showSnackBar(requireView(), "الرجاء إدخال بريد إلكتروني صحيح", true)
                false
            }
            else -> true
        }
    }

    private fun navigateToContainer() {
        findNavController().navigate(
            R.id.containerFragment,
            null,
            NavOptions.Builder().setPopUpTo(R.id.LoginFragment, true).setLaunchSingleTop(true).build()
        )
    }

    private fun showSnackBar(view: View, message: String, isError: Boolean) {
        val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        if (isError) {
            snackBarView.setBackgroundColor(resources.getColor(R.color.red, null))
            snackBar.setTextColor(resources.getColor(R.color.white, null))
        }
        snackBar.show()
    }

    private fun showLoading(isShown: Boolean) {
        binding.loadingView.root.isVisible = isShown
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
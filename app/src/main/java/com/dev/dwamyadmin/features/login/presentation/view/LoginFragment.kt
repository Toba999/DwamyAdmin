package com.dev.dwamyadmin.features.login.presentation.view

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.dev.dwamyadmin.R
import com.dev.dwamyadmin.databinding.FragmentLoginBinding
import com.dev.dwamyadmin.features.login.presentation.viewModel.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels()

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupListeners()
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
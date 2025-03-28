package com.dev.dwamyadmin.features.register.view

import android.R.attr.text
import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dev.dwamyadmin.R
import com.dev.dwamyadmin.databinding.FragmentRegisterBinding
import com.dev.dwamyadmin.features.register.models.RegisterState
import com.dev.dwamyadmin.features.register.viewModel.RegisterViewModel
import com.dev.dwamyadmin.utils.SharedPrefManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private val viewModel: RegisterViewModel by viewModels()
    private val args: RegisterFragmentArgs by navArgs()
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private var imageUri: Uri? = null
    private val selectedDays = BooleanArray(7)
    private val daysOfWeek = arrayOf("الأحد", "الإثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة", "السبت")
    private var adminId : String? = null
    private var latitude : Double? = null
    private var longitude : Double? = null
    private var address : String? = null
    var isAdmin : Boolean? = null

    @Inject
    lateinit var sharedPrefManager: SharedPrefManager

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            binding.uploadedImage.setImageURI(it)
            viewModel.uploadEmployeeImage(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isAdmin = args.isAdmin
        setupUI(isAdmin == true)
        setupListeners(isAdmin == true)
        observeViewModel()
        parentFragmentManager.setFragmentResultListener("locationRequestKey", viewLifecycleOwner) { _, bundle ->
            latitude = bundle.getDouble("latitude")
            longitude = bundle.getDouble("longitude")
            address = bundle.getString("address")
            binding.adminLocation.setText(address)
        }
    }

    private fun setupUI(isAdmin: Boolean) {
        if (isAdmin) {
            binding.timeRangeSlider.visibility = View.GONE
            binding.timeTitle.visibility = View.GONE
            binding.workDays.visibility = View.GONE
            binding.adminLocation.visibility = View.GONE
            binding.uploadImgButton.visibility = View.GONE
            binding.uploadedImage.visibility = View.GONE
            binding.empProfession.visibility = View.GONE
            binding.registerTitle.text = "تسجيل حساب"
        }
    }

    private fun setupListeners(isAdmin: Boolean) {
        binding.registerBackBtn.setOnClickListener {
            activity?.finish()
        }

        binding.workDays.setOnClickListener {
            showDaysPickerDialog()
        }
        binding.adminLocation.setOnClickListener {
            findNavController().navigate(R.id.action_registerEmployeeFragment_to_mapFragment)
        }

        binding.timeRangeSlider.setValues(9f, 17f)
        binding.timeRangeSlider.addOnChangeListener { slider, _, _ ->
            if (slider.values.size < 2) return@addOnChangeListener
            val values = slider.values
            val startHour = values[0].toInt()
            val endHour = values[1].toInt()
            binding.timeTitle.text = "وقت العمل من ${formatTime(startHour)} إلى ${formatTime(endHour)}"
        }

        binding.uploadImgButton.setOnClickListener {
            showImageSourceDialog()
        }

        binding.registerButton.setOnClickListener {
            if (validateInputs(isAdmin)) {
                if (isAdmin) {
                    viewModel.registerAdmin(
                        binding.adminName.text.toString().trim(),
                        binding.adminEmail.text.toString().trim(),
                        binding.adminPassword.text.toString().trim()
                    )
                } else {
                    adminId = sharedPrefManager.getAdminId()
                    viewModel.registerEmployee(
                        binding.adminName.text.toString().trim(),
                        binding.adminEmail.text.toString().trim(),
                        binding.empProfession.text.toString().trim(),
                        binding.adminPassword.text.toString().trim(),
                        adminId ?: "",
                        binding.workDays.text.toString(),
                        binding.timeRangeSlider.values[0].toInt(),
                        binding.timeRangeSlider.values[1].toInt(),
                        imageUri.toString(),
                        address.toString(),
                        latitude ?: 0.0,
                        longitude?: 0.0,
                        binding.locationArea.text.toString().toInt()
                    )
                }
            }
        }
    }

    private fun validateInputs(isAdmin: Boolean): Boolean {
        var isValid = true

        val name = binding.adminName.text.toString().trim()
        val email = binding.adminEmail.text.toString().trim()
        val password = binding.adminPassword.text.toString().trim()
        val profession = binding.empProfession.text.toString().trim()
        val area = binding.locationArea.text.toString()

        // Validate name (Required for both admin & employee)
        if (name.isEmpty()) {
            binding.adminName.error = "الاسم مطلوب"
            isValid = false
        } else {
            binding.adminName.error = null
        }

        // Validate email (Required for both admin & employee)
        if (email.isEmpty()) {
            binding.adminEmail.error = "البريد الإلكتروني مطلوب"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.adminEmail.error = "البريد الإلكتروني غير صالح"
            isValid = false
        } else {
            binding.adminEmail.error = null
        }

        // Validate password (Required for both admin & employee)
        if (password.isEmpty()) {
            binding.adminPassword.error = "كلمة المرور مطلوبة"
            isValid = false
        } else if (password.length < 6) {
            binding.adminPassword.error = "يجب أن تحتوي كلمة المرور على 6 أحرف على الأقل"
            isValid = false
        } else {
            binding.adminPassword.error = null
        }

        // If the user is an admin, return the result without checking other fields
        if (isAdmin) return isValid

        // Validate profession (Required only for employees)
        if (profession.isEmpty()) {
            binding.empProfession.error = "الوظيفة مطلوبة"
            isValid = false
        } else {
            binding.empProfession.error = null
        }

        // Validate area (Required only for employees)
        if (area.isEmpty()) {
            binding.locationArea.error = "المساحة مطلوبة"
            isValid = false
        } else {
            binding.locationArea.error = null
        }

        // Validate location fields (Required only for employees)
        if (latitude == null || longitude == null || address.isNullOrEmpty()) {
            binding.adminEmail.error = "الموقع مطلوب"
            isValid = false
        }

        return isValid
    }

    private fun showDaysPickerDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("اختر أيام الأسبوع")
            .setMultiChoiceItems(daysOfWeek, selectedDays) { _, which, isChecked ->
                selectedDays[which] = isChecked
            }
            .setPositiveButton("تم") { _, _ ->
                val selected = daysOfWeek.indices
                    .filter { selectedDays[it] }
                    .joinToString(", ") { daysOfWeek[it] }
                binding.workDays.setText(if (selected.isEmpty()) "اختر أيام الأسبوع" else selected)
            }
            .setNegativeButton("إلغاء", null)
            .show()
    }

    private fun formatTime(hour: Int): String {
        val period = if (hour >= 12) "PM" else "AM"
        val displayHour = if (hour % 12 == 0) 12 else hour % 12
        return String.format(Locale.getDefault(), "%02d:00 %s", displayHour, period)
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("اختر من المعرض")
        AlertDialog.Builder(requireContext())
            .setTitle("رفع صورة")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        galleryLauncher.launch("image/*")
                        binding.imageProgressBar.visibility = View.VISIBLE
                        binding.uploadedImage.visibility = View.GONE
                    }
                }
            }
            .show()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.registerState.collectLatest { state ->
                when (state) {
                    is RegisterState.Loading -> {
                        showLoading(true)
                    }
                    is RegisterState.Success -> {
                        navigateToContainer(state.message)
                    }
                    is RegisterState.Failure -> {
                        showLoading(false)
                        showSnackBar(requireView(), state.error, true)
                    }
                    is RegisterState.Idle -> {}
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.imageUrl.collectLatest { url ->
                binding.imageProgressBar.visibility = View.GONE
                binding.uploadedImage.visibility = View.VISIBLE
                imageUri = url?.toUri()
            }
        }
    }

    private fun navigateToContainer(message: String) {
        if (isAdmin == true){
            showSnackBar(requireView(),message,false)
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.registerFragment, true)
                .setLaunchSingleTop(true)
                .build()
            findNavController().navigate(R.id.containerFragment, null, navOptions)
        }else{
            showSnackBar(requireView(),message,false)
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.registerFragment, true)
                .setLaunchSingleTop(true)
                .build()
            findNavController().navigate(R.id.action_registerEmployeeFragment_to_homeFragment, null, navOptions)
        }

    }
    private fun showSnackBar(view: View, message: String, isError: Boolean) {
        val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        if (isError) {
            snackBarView.setBackgroundColor(resources.getColor(R.color.red, null))
            snackBar.setTextColor(resources.getColor(R.color.white, null))
        }else{
            snackBarView.setBackgroundColor(resources.getColor(R.color.green, null))
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
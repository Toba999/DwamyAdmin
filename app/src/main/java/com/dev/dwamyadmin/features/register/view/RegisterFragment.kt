package com.dev.dwamyadmin.features.register.view

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
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

    @Inject
    lateinit var sharedPrefManager: SharedPrefManager

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            imageUri = it
            binding.uploadedImage.setImageURI(it)
        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && imageUri != null) {
            binding.uploadedImage.setImageURI(imageUri)
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

        val isAdmin = args.isAdmin

        setupUI(isAdmin)
        setupListeners(isAdmin)
        observeViewModel()
    }

    private fun setupUI(isAdmin: Boolean) {
        if (isAdmin) {
            binding.timeRangeSlider.visibility = View.GONE
            binding.timeTitle.visibility = View.GONE
            binding.workDays.visibility = View.GONE
            binding.adminLocation.visibility = View.GONE
            binding.uploadImgButton.visibility = View.GONE
            binding.uploadedImage.visibility = View.GONE
        }
    }

    private fun setupListeners(isAdmin: Boolean) {
        binding.registerBackBtn.setOnClickListener {
            activity?.finish()
        }

        binding.workDays.setOnClickListener {
            showDaysPickerDialog()
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
            if (validateInputs()) {
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
                        binding.adminPassword.text.toString().trim(),
                        adminId ?: "",
                        binding.workDays.text.toString(),
                        binding.timeRangeSlider.values[0].toInt(),
                        binding.timeRangeSlider.values[1].toInt(),
                        imageUri.toString()
                    )
                }
            }
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        val name = binding.adminName.text.toString().trim()
        val email = binding.adminEmail.text.toString().trim()
        val password = binding.adminPassword.text.toString().trim()

        if (name.isEmpty()) {
            binding.adminName.error = "الاسم مطلوب"
            isValid = false
        } else {
            binding.adminName.error = null
        }

        if (email.isEmpty()) {
            binding.adminEmail.error = "البريد الإلكتروني مطلوب"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.adminEmail.error = "البريد الإلكتروني غير صالح"
            isValid = false
        } else {
            binding.adminEmail.error = null
        }

        if (password.isEmpty()) {
            binding.adminPassword.error = "كلمة المرور مطلوبة"
            isValid = false
        } else if (password.length < 6) {
            binding.adminPassword.error = "يجب أن تحتوي كلمة المرور على 6 أحرف على الأقل"
            isValid = false
        } else {
            binding.adminPassword.error = null
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
        val options = arrayOf("اختر من المعرض", "التقط صورة")
        AlertDialog.Builder(requireContext())
            .setTitle("رفع صورة")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> galleryLauncher.launch("image/*")
                    1 -> takePhoto()
                }
            }
            .show()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.registerState.collectLatest { state ->
                when (state) {
                    is RegisterState.Loading -> {
                        showLoading(true)
                    }
                    is RegisterState.Success -> {
                        navigateToContainer()
                    }
                    is RegisterState.Failure -> {
                        showLoading(false)
                        showSnackBar(requireView(), state.error, true)
                    }
                    is RegisterState.Idle -> {}
                }
            }
        }
    }

    private fun navigateToContainer() {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.registerFragment, true)
            .setLaunchSingleTop(true)
            .build()
        findNavController().navigate(R.id.containerFragment, null, navOptions)
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
    private fun takePhoto() {
        imageUri = createImageUri()
        imageUri?.let { uri ->
            cameraLauncher.launch(uri)
        }
    }

    private fun createImageUri(): Uri? {
        val file = File(requireContext().filesDir, "photo_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", file)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
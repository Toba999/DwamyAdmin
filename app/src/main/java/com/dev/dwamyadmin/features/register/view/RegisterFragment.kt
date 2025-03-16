package com.dev.dwamyadmin.features.register.view

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.dev.dwamyadmin.R
import com.dev.dwamyadmin.databinding.FragmentRegisterBinding
import com.dev.dwamyadmin.features.register.viewModel.RegisterViewModel
import com.google.android.material.slider.RangeSlider
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.Locale
import kotlin.math.roundToInt

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private val viewModel: RegisterViewModel by viewModels()
    private var _binding: FragmentRegisterBinding? = null
    private val selectedDays = BooleanArray(7)
    private val daysOfWeek = arrayOf("الأحد", "الإثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة", "السبت")

    private val binding get() = _binding!!
    private var imageUri: Uri? = null

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
        _binding =
            FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.registerBackBtn.setOnClickListener{
            activity?.finish()
        }
        binding.workDays.setOnClickListener {
            showDaysPickerDialog()
        }
        binding.timeRangeSlider.setValues(9f, 17f)
        binding.timeRangeSlider.addOnChangeListener { slider, _, _ ->
            if (slider.values.size < 2) return@addOnChangeListener // Prevent crash
            val values = slider.values
            val startHour = values[0].toInt()
            val endHour = values[1].toInt()

            val start = formatTime(startHour)
            val end = formatTime(endHour)

            binding.timeTitle.text = "وقت العمل " + "من $start إلى $end"
        }
        binding.uploadImgButton.setOnClickListener {
            showImageSourceDialog()
        }
        binding.registerButton.setOnClickListener {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.registerFragment, true)
                .setLaunchSingleTop(true)
                .build()

            findNavController().navigate(R.id.containerFragment, null, navOptions)
        }
       // binding.adminName.text.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun showDaysPickerDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("اختر أيام الأسبوع")

        builder.setMultiChoiceItems(daysOfWeek, selectedDays) { _, which, isChecked ->
            selectedDays[which] = isChecked
        }

        builder.setPositiveButton("تم") { _, _ ->
            val selected = daysOfWeek.indices
                .filter { selectedDays[it] }
                .joinToString(", ") { daysOfWeek[it] }  // Join with a comma and space

            binding.workDays.setText(if (selected.isEmpty()) "اختر أيام الأسبوع" else selected)
        }

        builder.setNegativeButton("إلغاء", null)
        builder.show()
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
                    0 -> galleryLauncher.launch("image/*")  // Open Gallery
                    1 -> takePhoto()  // Capture Photo
                }
            }
            .show()
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
}
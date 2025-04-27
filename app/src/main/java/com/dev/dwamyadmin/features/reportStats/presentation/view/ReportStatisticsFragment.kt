package com.dev.dwamyadmin.features.reportStats.presentation.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dev.dwamyadmin.databinding.FragmentReportStatisticsBinding
import com.dev.dwamyadmin.domain.models.Employee
import com.dev.dwamyadmin.features.reportStats.presentation.viewModel.AttendanceCountUiState
import com.dev.dwamyadmin.features.reportStats.presentation.viewModel.ReportsStatsViewModel
import com.dev.dwamyadmin.features.reports.presentation.viewModel.ReportsUiState
import com.dev.dwamyadmin.utils.SharedPrefManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@AndroidEntryPoint
class ReportStatisticsFragment : Fragment() {

    private var _binding: FragmentReportStatisticsBinding? = null
    private val binding get() = _binding!!


    private val viewModel: ReportsStatsViewModel by viewModels()

    @Inject
    lateinit var sharedPrefManager: SharedPrefManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adminId = sharedPrefManager.getAdminId()
        viewModel.getAttendanceCountPerEmployeePerMonth(adminId.toString())
        observeViewModel()
        binding.reportBackBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.attendanceCountState.collect { state ->
                when (state) {
                    is AttendanceCountUiState.Loading -> {
                        showLoading(true)
                        binding.emptyStateTv.visibility = View.GONE
                        binding.reportsRv.visibility = View.GONE
                    }
                    is AttendanceCountUiState.Success -> {
                        showLoading(false)
                        binding.reportsRv.visibility = View.VISIBLE
                        binding.emptyStateTv.visibility = View.GONE
                        Log.e("AttendanceCountUiState", "Success: ${state.attendanceCount}")
                        state.attendanceCount.forEach { (employee, monthAttendanceMap) ->
                            // Get attendance count for April
                            val attendanceCount = monthAttendanceMap["2025-04"] ?: 0

                            // Employee's work days (split by comma and spaces if needed)
                            val workDays = employee.workDays.split(",").map { it.trim() }

                            // Calculate attendance percentage
                            val percentage = calculateAttendancePercentage("2025-04", workDays, attendanceCount)

                            Log.e("Employee", "Employee: ${employee.name}, Attendance Percentage for April 2025: ${"%.2f".format(percentage)}%")
                        }
                    }
                    is AttendanceCountUiState.Error -> {
                        showLoading(false)
                        binding.emptyStateTv.visibility = View.VISIBLE
                        binding.reportsRv.visibility = View.GONE
                        showToast(state.message)
                    }
                    is AttendanceCountUiState.Empty -> {
                        showLoading(false)
                        binding.emptyStateTv.visibility = View.VISIBLE
                        binding.reportsRv.visibility = View.GONE
                        showToast("لا توجد سجلات حضور")
                    }
                    else -> Unit
                }
            }
        }
    }
    private fun showLoading(isVisible: Boolean) {
        binding.loadingView.root.isVisible = isVisible
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }


    fun calculateAttendancePercentage(
        yearMonth: String, // example: "2025-04"
        workDays: List<String>, // example: listOf("الأحد", "الإثنين", "الثلاثاء", "الأربعاء", "الخميس")
        attendanceCount: Int
    ): Double {
        val (year, month) = yearMonth.split("-").map { it.toInt() }
        val daysInMonth = YearMonth.of(year, month).lengthOfMonth()

        // Map Arabic weekdays to English DayOfWeek enums
        val arabicToDayOfWeek = mapOf(
            "الأحد" to DayOfWeek.SUNDAY,
            "الإثنين" to DayOfWeek.MONDAY,
            "الثلاثاء" to DayOfWeek.TUESDAY,
            "الأربعاء" to DayOfWeek.WEDNESDAY,
            "الخميس" to DayOfWeek.THURSDAY,
            "الجمعة" to DayOfWeek.FRIDAY,
            "السبت" to DayOfWeek.SATURDAY
        )

        // Convert workDays to set of DayOfWeek
        val workingDayEnums = workDays.mapNotNull { arabicToDayOfWeek[it] }.toSet()

        // Count how many working days in that month
        var totalWorkDaysInMonth = 0
        for (day in 1..daysInMonth) {
            val date = LocalDate.of(year, month, day)
            if (date.dayOfWeek in workingDayEnums) {
                totalWorkDaysInMonth++
            }
        }

        // Avoid division by zero
        if (totalWorkDaysInMonth == 0) return 0.0

        // Calculate the attendance percentage
        return (attendanceCount.toDouble() / totalWorkDaysInMonth) * 100
    }
}
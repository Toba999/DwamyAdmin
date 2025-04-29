package com.dev.dwamyadmin.features.reportStats.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dev.dwamyadmin.databinding.FragmentReportStatisticsBinding
import com.dev.dwamyadmin.domain.models.AttendanceStats
import com.dev.dwamyadmin.features.reportStats.presentation.viewModel.AttendanceCountUiState
import com.dev.dwamyadmin.features.reportStats.presentation.viewModel.ReportsStatsViewModel
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

    private val months = listOf(
        "يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو",
        "يوليو", "أغسطس", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"
    )

    private val viewModel: ReportsStatsViewModel by viewModels()
    private lateinit var reportsAdapter: ReportsStatsAdapter

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

        setupRecyclerView()
        observeViewModel()

        binding.reportBackBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.filterBtn.setOnClickListener {
            binding.filterOptionsContainer.isVisible = !binding.filterOptionsContainer.isVisible
        }

        binding.monthRadio.setOnCheckedChangeListener { _, isChecked ->
            binding.monthSpinner.isVisible = isChecked
            if (isChecked) {
                val selectedMonthIndex = binding.monthSpinner.selectedItemPosition + 1
                val currentYear = LocalDate.now().year
                val formattedMonth = if (selectedMonthIndex < 10) "0$selectedMonthIndex" else "$selectedMonthIndex"
                val selectedDatePrefix = "$currentYear-$formattedMonth"
                filterAttendanceList(selectedDatePrefix)
            }
        }

        binding.yearRadio.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.monthSpinner.isVisible = false
                val currentYear = LocalDate.now().year
                filterAttendanceByYear(currentYear)
            }
        }

        binding.monthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (binding.monthRadio.isChecked) {
                    val selectedMonthIndex = position + 1
                    val currentYear = LocalDate.now().year
                    val formattedMonth = if (selectedMonthIndex < 10) "0$selectedMonthIndex" else "$selectedMonthIndex"
                    val selectedDatePrefix = "$currentYear-$formattedMonth"
                    filterAttendanceList(selectedDatePrefix)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupRecyclerView() {
        reportsAdapter = ReportsStatsAdapter(mutableListOf())
        binding.reportsRv.adapter = reportsAdapter

        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, months)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.monthSpinner.adapter = spinnerAdapter
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

                        val attendanceStatsList = state.attendanceCount.map { (employee, _) ->
                            AttendanceStats(
                                employeeName = employee.name,
                                profession = employee.profession,
                                percentageTime = "",
                                percentage = ""
                            )
                        }

                        reportsAdapter.updateList(attendanceStatsList.toMutableList())
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

    private fun calculateAttendancePercentage(
        yearMonth: String,
        workDays: List<String>,
        attendanceCount: Int
    ): Double {
        val (year, month) = yearMonth.split("-").map { it.toInt() }
        val daysInMonth = YearMonth.of(year, month).lengthOfMonth()

        val arabicToDayOfWeek = mapOf(
            "الأحد" to DayOfWeek.SUNDAY,
            "الإثنين" to DayOfWeek.MONDAY,
            "الثلاثاء" to DayOfWeek.TUESDAY,
            "الأربعاء" to DayOfWeek.WEDNESDAY,
            "الخميس" to DayOfWeek.THURSDAY,
            "الجمعة" to DayOfWeek.FRIDAY,
            "السبت" to DayOfWeek.SATURDAY
        )

        val workingDayEnums = workDays.mapNotNull { arabicToDayOfWeek[it] }.toSet()

        var totalWorkDaysInMonth = 0
        for (day in 1..daysInMonth) {
            val date = LocalDate.of(year, month, day)
            if (date.dayOfWeek in workingDayEnums) {
                totalWorkDaysInMonth++
            }
        }

        if (totalWorkDaysInMonth == 0) return 0.0

        return (attendanceCount.toDouble() / totalWorkDaysInMonth) * 100
    }

    private fun filterAttendanceList(selectedDatePrefix: String) {
        val filteredList = mutableListOf<AttendanceStats>()
        val (year, month) = selectedDatePrefix.split("-").map { it.toInt() }
        val monthName = months[month - 1]

        viewModel.attendanceCountState.value.let { state ->
            if (state is AttendanceCountUiState.Success) {
                state.attendanceCount.forEach { (employee, monthAttendanceMap) ->
                    val attendanceCount = monthAttendanceMap[selectedDatePrefix] ?: 0
                    val workDays = employee.workDays.split(",").map { it.trim() }
                    val percentage = calculateAttendancePercentage(selectedDatePrefix, workDays, attendanceCount)

                    val attendanceStats = AttendanceStats(
                        employeeName = employee.name,
                        profession = employee.profession,
                        percentageTime = "$monthName $year",
                        percentage = "%.2f".format(percentage) + "%"
                    )
                    filteredList.add(attendanceStats)
                }

                reportsAdapter.updateList(filteredList)
            }
        }
    }
    private fun filterAttendanceByYear(year: Int) {
        val filteredList = mutableListOf<AttendanceStats>()

        viewModel.attendanceCountState.value.let { state ->
            if (state is AttendanceCountUiState.Success) {
                state.attendanceCount.forEach { (employee, monthAttendanceMap) ->
                    val workDays = employee.workDays.split(",").map { it.trim() }

                    // Sum attendance for all months of the given year
                    var totalAttendance = 0
                    for (month in 1..12) {
                        val formattedMonth = if (month < 10) "0$month" else "$month"
                        val key = "$year-$formattedMonth"
                        totalAttendance += monthAttendanceMap[key] ?: 0
                    }

                    val totalWorkDaysInYear = calculateTotalWorkDaysInYear(year, workDays)

                    val percentage = if (totalWorkDaysInYear > 0)
                        (totalAttendance.toDouble() / totalWorkDaysInYear) * 100
                    else 0.0

                    val attendanceStats = AttendanceStats(
                        employeeName = employee.name,
                        profession = employee.profession,
                        percentageTime = "عام $year",
                        percentage = "%.2f".format(percentage) + "%"
                    )

                    filteredList.add(attendanceStats)
                }

                reportsAdapter.updateList(filteredList)
            }
        }
    }
    private fun calculateTotalWorkDaysInYear(year: Int, workDays: List<String>): Int {
        val arabicToDayOfWeek = mapOf(
            "الأحد" to DayOfWeek.SUNDAY,
            "الإثنين" to DayOfWeek.MONDAY,
            "الثلاثاء" to DayOfWeek.TUESDAY,
            "الأربعاء" to DayOfWeek.WEDNESDAY,
            "الخميس" to DayOfWeek.THURSDAY,
            "الجمعة" to DayOfWeek.FRIDAY,
            "السبت" to DayOfWeek.SATURDAY
        )
        val workingDayEnums = workDays.mapNotNull { arabicToDayOfWeek[it] }.toSet()

        var totalWorkDays = 0
        for (month in 1..12) {
            val daysInMonth = YearMonth.of(year, month).lengthOfMonth()
            for (day in 1..daysInMonth) {
                val date = LocalDate.of(year, month, day)
                if (date.dayOfWeek in workingDayEnums) {
                    totalWorkDays++
                }
            }
        }
        return totalWorkDays
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.dev.dwamyadmin.features.reports.presentation.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dev.dwamyadmin.databinding.FragmentReportsBinding
import com.dev.dwamyadmin.domain.models.EmployeeAttendence
import com.google.firebase.Timestamp
import com.sahana.horizontalcalendar.HorizontalCalendar
import com.sahana.horizontalcalendar.OnDateSelectListener
import com.sahana.horizontalcalendar.model.DateModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class ReportsFragment : Fragment() {
    private lateinit var binding: FragmentReportsBinding
    private lateinit var reportsAdapter: ReportsAdapter
    private lateinit var horizontalCalendar: HorizontalCalendar
    private lateinit var selectedDate: DateModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReportsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectedDate = getCurrentDateModel()
        setupRecyclerView()
        setupCalendar()
        loadDataForSelectedDate()
    }

    private fun setupRecyclerView() {
        reportsAdapter = ReportsAdapter(mutableListOf())
        binding.reportsRv.adapter = reportsAdapter
    }

    private fun getCurrentDateModel(): DateModel {
        val calendar = Calendar.getInstance()
        return DateModel().apply {
            day = calendar.get(Calendar.DAY_OF_MONTH)
            year = calendar.get(Calendar.YEAR)
            monthNumber = calendar.get(Calendar.MONTH) + 1
            month = SimpleDateFormat("MMM", Locale.getDefault()).format(calendar.time)
            dayOfWeek = SimpleDateFormat("EEE", Locale.getDefault()).format(calendar.time)
        }
    }

    private fun setupCalendar() {
        horizontalCalendar = binding.calenderView
        horizontalCalendar.setOnDateSelectListener(OnDateSelectListener { dateModel ->
            selectedDate = dateModel
            loadDataForSelectedDate()
        })

    }

    private fun loadDataForSelectedDate() {
        binding.emptyStateTv.visibility = View.GONE
        binding.reportsRv.visibility = View.GONE

        Handler(Looper.getMainLooper()).postDelayed({
            val employeeList = getEmployeeAttendanceForDate(selectedDate)
            reportsAdapter.updateList(employeeList)
            updateEmptyStateVisibility(employeeList.isEmpty())
        }, 1000)
    }

    private fun getEmployeeAttendanceForDate(dateModel: DateModel): List<EmployeeAttendence> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, dateModel.year)
            set(Calendar.MONTH, dateModel.monthNumber - 1)
            set(Calendar.DAY_OF_MONTH, dateModel.day)
        }

        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        return when {
            // Weekend - no data
            dayOfWeek == Calendar.FRIDAY || dayOfWeek == Calendar.SATURDAY -> emptyList()

            // First day of month - special schedule
            dayOfMonth == 1 -> listOf(
                EmployeeAttendence(
                    id = "001",
                    name = "أحمد محمد",
                    profession = "مدير",
                    checkIn = Timestamp(createTime(calendar, 8, 30)),
                    checkOut = Timestamp(createTime(calendar, 15, 30))
                ),
                EmployeeAttendence(
                    id = "002",
                    name = "سارة علي",
                    profession = "سكرتيرة",
                    checkIn = Timestamp(createTime(calendar, 9, 15)),
                    checkOut = Timestamp(createTime(calendar, 16, 45))
                )
            )

            // Mid-month - normal schedule
            dayOfMonth in 10..20 -> listOf(
                EmployeeAttendence(
                    id = "003",
                    name = "خالد عبدالله",
                    profession = "مطور",
                    checkIn = Timestamp(createTime(calendar, 9, 0)),
                    checkOut = Timestamp(createTime(calendar, 17, 0))
                ),
                EmployeeAttendence(
                    id = "004",
                    name = "نورة أحمد",
                    profession = "مصممة",
                    checkIn = Timestamp(createTime(calendar, 8, 45)),
                    checkOut = Timestamp(createTime(calendar, 16, 30))
                ),
                EmployeeAttendence(
                    id = "005",
                    name = "عمر فاروق",
                    profession = "مدير مشاريع",
                    checkIn = Timestamp(createTime(calendar, 10, 0)),
                    checkOut = Timestamp(createTime(calendar, 18, 0))
                )
            )

            // End of month - reduced staff
            else -> listOf(
                EmployeeAttendence(
                    id = "006",
                    name = "ليلى كمال",
                    profession = "محاسبة",
                    checkIn = Timestamp(createTime(calendar, 8, 0)),
                    checkOut = Timestamp(createTime(calendar, 16, 0))
                ),
                EmployeeAttendence(
                    id = "007",
                    name = "ياسر وليد",
                    profession = "مدير مبيعات",
                    checkIn = Timestamp(createTime(calendar, 10, 30)),
                    checkOut = Timestamp(createTime(calendar, 19, 0))
                )
            )
        }
    }

    private fun createTime(baseCalendar: Calendar, hour: Int, minute: Int): Date {
        return Calendar.getInstance().apply {
            set(Calendar.YEAR, baseCalendar.get(Calendar.YEAR))
            set(Calendar.MONTH, baseCalendar.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, baseCalendar.get(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
    }

    private fun updateEmptyStateVisibility(isEmpty: Boolean) {
        binding.emptyStateTv.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.reportsRv.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }
}
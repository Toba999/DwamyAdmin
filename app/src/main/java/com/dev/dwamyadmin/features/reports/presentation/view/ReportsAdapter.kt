package com.dev.dwamyadmin.features.reports.presentation.view

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dev.dwamyadmin.databinding.ReportsItemBinding
import com.dev.dwamyadmin.domain.models.EmployeeAttendence
import java.util.Calendar
import java.util.Date

class ReportsAdapter(
    private val employeesList: MutableList<EmployeeAttendence>, // Use MutableList for dynamic updates
) : RecyclerView.Adapter<ReportsAdapter.ReportsViewHolder>() {

    inner class ReportsViewHolder(private val binding: ReportsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(employee: EmployeeAttendence) {
            binding.empName.text = employee.name
            binding.empProf.text = employee.profession

            // Format check-in time
            val checkInTime = formatTime(employee.checkIn.toDate(), "حضور في ")
            binding.empClockIn.text = checkInTime
            binding.empClockIn.setTextColor(Color.GREEN) // Set text color to green

            // Format check-out time
            val checkOutTime = formatTime(employee.checkOut.toDate(), "انصراف في ")
            binding.empClockOut.text = checkOutTime
        }

        private fun formatTime(date: Date, prefix: String): String {
            val calendar = Calendar.getInstance()
            calendar.time = date

            val hour = calendar.get(Calendar.HOUR)
            val minute = calendar.get(Calendar.MINUTE)
            val amPm = if (calendar.get(Calendar.AM_PM) == Calendar.AM) "ص" else "م"

            // Format minute with leading zero if needed
            val minuteStr = if (minute < 10) "0$minute" else minute.toString()

            return "$prefix$hour:$minuteStr $amPm"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ReportsItemBinding.inflate(inflater, parent, false)
        return ReportsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReportsViewHolder, position: Int) {
        holder.bind(employeesList[position])
    }

    override fun getItemCount(): Int {
        return employeesList.size
    }


    fun updateList(newList: List<EmployeeAttendence>) {
        employeesList.clear()
        employeesList.addAll(newList)
        notifyDataSetChanged()
    }
}

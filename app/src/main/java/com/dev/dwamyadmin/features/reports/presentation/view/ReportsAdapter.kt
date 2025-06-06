package com.dev.dwamyadmin.features.reports.presentation.view

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dev.dwamyadmin.databinding.ReportsItemBinding
import com.dev.dwamyadmin.domain.models.Attendance
import com.dev.dwamyadmin.domain.models.EmployeeAttendence
import java.util.Calendar
import java.util.Date

class ReportsAdapter(
    private val employeesList: MutableList<Attendance>,
) : RecyclerView.Adapter<ReportsAdapter.ReportsViewHolder>() {

    inner class ReportsViewHolder(private val binding: ReportsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(employee: Attendance) {
            binding.empName.text = employee.employeeName
            binding.empProf.text = employee.profession

            val checkInTime = formatTime(
                employee.checkInTime.toString(),
                "حضور في "
            )
            binding.empClockIn.text = checkInTime
            binding.empClockIn.setTextColor(Color.GREEN)

            val checkOutTime = formatTime(
                employee.checkOutTime.toString(),
                "انصراف في "
            )
            binding.empClockOut.text = checkOutTime
            binding.empClockIn.setTextColor(
                formatTimeColor(
                    employee.checkInTime.toString(),
                    employee.startTime,
                    employee.endTime,
                    true
                )
            )
            binding.empClockOut.setTextColor(
                formatTimeColor(
                    employee.checkOutTime.toString(),
                    employee.startTime,
                    employee.endTime,
                    false
                )
            )
        }

        private fun formatTime(
            timeStr: String,
            prefix: String
        ): String {
            try {
                // Parse the provided time (e.g., "12:03")
                val parts = timeStr.split(":")
                if (parts.size != 2) return "$prefix N/A"

                val hour = parts[0].toInt()
                val minute = parts[1].toInt()

                // Convert to 12-hour format with AM/PM
                val isAM = hour < 12
                val formattedHour = if (hour % 12 == 0) 12 else hour % 12
                val minuteStr = minute.toString().padStart(2, '0')
                val amPm = if (isAM) "ص" else "م"
                return "$prefix $formattedHour:$minuteStr $amPm"
            } catch (e: Exception) {
                e.printStackTrace()
                return "$prefix لم يسجل بعد "
            }
        }

        private fun formatTimeColor(
            timeStr: String,
            attendTime: String,
            leaveTime: String,
            isAttend: Boolean
        ): Int {
            try {
                // Parse the provided time (e.g., "12:03")
                val parts = timeStr.split(":")

                val hour = parts[0].toInt()
                val minute = parts[1].toInt()


                // Check if before attendance or after leaving
                val attendHour = attendTime.toIntOrNull() ?: 9   // Default 9 AM
                val leaveHour = leaveTime.toIntOrNull() ?: 18   // Default 6 PM
                val isAfterAttend = hour > attendHour || (hour == attendHour && minute > 0)
                val isBeforeLeave = hour < leaveHour

                return if (isAttend) {
                    when {
                        isAfterAttend -> Color.RED  // Early check-in
                        else -> Color.GREEN         // Normal case
                    }
                } else {
                    when {
                        isBeforeLeave -> Color.RED   // Late check-out
                        else -> Color.GREEN         // Normal case
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                return Color.RED
            }
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


    fun updateList(newList: List<Attendance>) {
        employeesList.clear()
        employeesList.addAll(newList)
        notifyDataSetChanged()
    }
}

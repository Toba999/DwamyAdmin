package com.dev.dwamyadmin.features.reportStats.presentation.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dev.dwamyadmin.databinding.ReportsStatsItemBinding
import com.dev.dwamyadmin.domain.models.AttendanceStats

class ReportsStatsAdapter(
    private val employeesList: MutableList<AttendanceStats>,
) : RecyclerView.Adapter<ReportsStatsAdapter.ReportsViewHolder>() {

    inner class ReportsViewHolder(private val binding: ReportsStatsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(employee: AttendanceStats) {
            binding.empName.text = employee.employeeName
            binding.empProf.text = employee.profession
            binding.timeTv.text = employee.percentageTime
            binding.percentageTv.text = employee.percentage
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ReportsStatsItemBinding.inflate(inflater, parent, false)
        return ReportsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReportsViewHolder, position: Int) {
        holder.bind(employeesList[position])
    }

    override fun getItemCount(): Int {
        return employeesList.size
    }


    fun updateList(newList: List<AttendanceStats>) {
        employeesList.clear()
        employeesList.addAll(newList)
        notifyDataSetChanged()
    }
}

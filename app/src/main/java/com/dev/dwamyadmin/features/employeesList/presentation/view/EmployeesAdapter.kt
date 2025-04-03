package com.dev.dwamyadmin.features.employeesList.presentation.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dev.dwamyadmin.databinding.EmployeeItemBinding
import com.dev.dwamyadmin.domain.models.Employee

class EmployeesAdapter(
    private val employeesList: MutableList<Employee>, // Use MutableList for dynamic updates
    private val onDeleteClickListener: (Employee) -> Unit,
    private val onEditClickListener: (Employee) -> Unit // Pass position for updates
) : RecyclerView.Adapter<EmployeesAdapter.EmployeeViewHolder>() {

    inner class EmployeeViewHolder(private val binding: EmployeeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(employee: Employee) {

            binding.empName.text = employee.name
            binding.empProf.text = employee.profession

            binding.deleteBtn.setOnClickListener {
                onDeleteClickListener(employee)
            }
            binding.btnEdit.setOnClickListener {
                onEditClickListener(employee)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = EmployeeItemBinding.inflate(inflater, parent, false)
        return EmployeeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        holder.bind(employeesList[position])
    }

    override fun getItemCount(): Int {
        return employeesList.size
    }


    fun updateList(newList: List<Employee>) {
        employeesList.clear()
        employeesList.addAll(newList)
        notifyDataSetChanged()
    }
}

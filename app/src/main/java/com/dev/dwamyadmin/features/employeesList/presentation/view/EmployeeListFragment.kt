package com.dev.dwamyadmin.features.employeesList.presentation.view

import android.os.Bundle
import android.util.Log.e
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev.dwamyadmin.databinding.FragmentEmployeeListBinding
import com.dev.dwamyadmin.domain.models.Employee
import com.dev.dwamyadmin.features.employeesList.presentation.viewModel.EmployeesViewModel
import com.dev.dwamyadmin.utils.SharedPrefManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class EmployeeListFragment : Fragment() {
    private val viewModel: EmployeesViewModel by viewModels()
    private var _binding: FragmentEmployeeListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: EmployeesAdapter

    @Inject
    lateinit var sharedPrefManager: SharedPrefManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmployeeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showLoading(true)
        setupRecyclerView()
        observeViewModel()
        binding.employeeBackBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        val adminId = sharedPrefManager.getAdminId()
        viewModel.fetchEmployees(adminId.toString())
    }

    private fun setupRecyclerView() {
        adapter = EmployeesAdapter(
            mutableListOf(),
            onDeleteClickListener = { employee -> handleDeleteClick(employee) }
        )

        binding.employeeRv.layoutManager = LinearLayoutManager(requireContext())
        binding.employeeRv.adapter = adapter
    }
    private fun handleDeleteClick(employee: Employee) {
        viewModel.deleteEmployee(employee.id)
    }
    private fun showLoading(isShown: Boolean) {
        binding.loadingView.root.isVisible = isShown
    }
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.employees.collect { employees ->
                employees?.let {
                    if (employees.isEmpty()) {
                        showLoading(false)
                        binding.emptyStateTv.visibility = View.VISIBLE
                        binding.emptyStateTv.text = "لا يوجد موظفين"
                        binding.employeeRv.visibility = View.GONE
                    } else {
                        showLoading(false)
                        binding.emptyStateTv.visibility = View.GONE
                        binding.employeeRv.visibility = View.VISIBLE
                        adapter.updateList(employees)
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.deleteEmployeeResult.collect { success ->
                success?.let {
                    if (success) {
                        showSnackBar("تم مسح الموظف بنجاح", false)
                    } else {
                        showSnackBar("فشل مسح الموظف", true)
                    }
                }
            }
        }
    }

    private fun showSnackBar(message: String, isError: Boolean) {
        val snackBar = Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        if (isError) {
            snackBarView.setBackgroundColor(resources.getColor(android.R.color.holo_red_dark, null))
        } else {
            snackBarView.setBackgroundColor(resources.getColor(android.R.color.holo_green_dark, null))
        }
        snackBar.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
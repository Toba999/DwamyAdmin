package com.dev.dwamyadmin.features.employeesList.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev.dwamyadmin.databinding.FragmentEmployeeListBinding
import com.dev.dwamyadmin.domain.models.Employee
import com.dev.dwamyadmin.features.employeesList.presentation.viewModel.EmployeesViewModel
import com.dev.dwamyadmin.utils.SharedPrefManager
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
        setFragmentResultListener("delete_request") { _, bundle ->
            val isDeleted = bundle.getBoolean("isDeleted")
            if (isDeleted) {
                viewModel.fetchEmployees(adminId.toString())
            }
        }
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
        findNavController().navigate(EmployeeListFragmentDirections.actionEmployeeListFragmentToDeleteDialogFragment(employee))
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
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.dev.dwamyadmin.features.reports.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dev.dwamyadmin.databinding.FragmentReportsBinding
import com.dev.dwamyadmin.features.reports.presentation.viewModel.ReportsUiState
import com.dev.dwamyadmin.features.reports.presentation.viewModel.ReportsViewModel
import com.dev.dwamyadmin.utils.SharedPrefManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import javax.inject.Inject


@AndroidEntryPoint
class ReportsFragment : Fragment() {

    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!

    private lateinit var reportsAdapter: ReportsAdapter

    private val viewModel: ReportsViewModel by viewModels()

    @Inject
    lateinit var sharedPrefManager: SharedPrefManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adminId = sharedPrefManager.getAdminId()
        viewModel.getEmployeesByDate(formatDateModel(getCurrentDateModel()), adminId.toString())
        setupRecyclerView()
        setupCalendar()
        observeViewModel()
        binding.reportBackBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.reportStateBtn.setOnClickListener {
            findNavController().navigate(ReportsFragmentDirections.actionReportsFragmentToReportStatisticsFragment())
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is ReportsUiState.Loading -> {
                        showLoading(true)
                        binding.emptyStateTv.visibility = View.GONE
                        binding.reportsRv.visibility = View.GONE
                    }
                    is ReportsUiState.Success -> {
                        showLoading(false)
                        binding.reportsRv.visibility = View.VISIBLE
                        binding.emptyStateTv.visibility = View.GONE
                        reportsAdapter.updateList(state.attendanceList)
                    }
                    is ReportsUiState.Error -> {
                        showLoading(false)
                        binding.emptyStateTv.visibility = View.VISIBLE
                        binding.reportsRv.visibility = View.GONE
                        showToast(state.message)
                    }
                    is ReportsUiState.Empty -> {
                        showLoading(false)
                        binding.emptyStateTv.visibility = View.VISIBLE
                        binding.reportsRv.visibility = View.GONE
                        reportsAdapter.updateList(emptyList())
                        showToast("لا توجد سجلات حضور")
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun setupRecyclerView() {
        reportsAdapter = ReportsAdapter(mutableListOf())
        binding.reportsRv.adapter = reportsAdapter
    }

    private fun setupCalendar() {
        binding.calenderView.onSelectionChanged = { date ->
            viewModel.getEmployeesByDate(
                formatDateModel(date),
                sharedPrefManager.getAdminId().toString()
            )
        }
    }

    private fun getCurrentDateModel(): Date {
        val calendar = Calendar.getInstance()
        return calendar.time
    }

    private fun formatDateModel(selectedDate: Date): String {
        val instant = selectedDate.toInstant()
        val zoneId = ZoneId.systemDefault()
        val localDate = instant.atZone(zoneId).toLocalDate()

        return localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }

    private fun showLoading(isVisible: Boolean) {
        binding.loadingView.root.isVisible = isVisible
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
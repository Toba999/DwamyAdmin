package com.dev.dwamyadmin.features.vacation.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev.dwamyadmin.databinding.FragmentVacationBinding
import com.dev.dwamyadmin.domain.models.LeaveRequest
import com.dev.dwamyadmin.domain.models.LeaveStatus
import com.dev.dwamyadmin.domain.repo.FireBaseRepo
import com.dev.dwamyadmin.features.vacation.presentation.viewModel.VacationViewModel
import com.dev.dwamyadmin.utils.SharedPrefManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class VacationFragment : Fragment() {
    private val viewModel: VacationViewModel by viewModels()
    private var _binding: FragmentVacationBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: VacationAdapter

    @Inject
    lateinit var sharedPrefManager: SharedPrefManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVacationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()


        val adminId = sharedPrefManager.getAdminId()
        viewModel.fetchLeaveRequests(adminId.toString())
    }

    private fun setupRecyclerView() {
        adapter = VacationAdapter(
            mutableListOf(),
            onAcceptClickListener = { vacationItem, position ->
                handleAcceptClick(vacationItem, position)
            },
            onDeclineClickListener = { vacationItem, position ->
                handleDeclineClick(vacationItem, position)
            }
        )

        binding.vacationRv.layoutManager = LinearLayoutManager(requireContext())
        binding.vacationRv.adapter = adapter
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.leaveRequests.collect { requests ->
                if (requests.isEmpty()) {
                    binding.emptyStateTv.visibility = View.VISIBLE
                    binding.emptyStateTv.text = "لا توجد طلبات إجازة"
                    binding.vacationRv.visibility = View.GONE
                } else {
                    binding.emptyStateTv.visibility = View.GONE
                    binding.vacationRv.visibility = View.VISIBLE
                    adapter.updateList(requests)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.updateStatusResult.collect { success ->
                success?.let {
                    if (success) {
                        showSnackBar("تم تحديث الحالة بنجاح", false)
                    } else {
                        showSnackBar("فشل في تحديث الحالة", true)
                    }
                }
            }
        }
    }

    private fun handleAcceptClick(vacationItem: VacationItem, position: Int) {
        viewModel.updateLeaveRequestStatus(vacationItem.id, LeaveStatus.ACCEPTED)
        adapter.updateItemStatus(position, "مقبول")
    }

    private fun handleDeclineClick(vacationItem: VacationItem, position: Int) {
        viewModel.updateLeaveRequestStatus(vacationItem.id, LeaveStatus.REJECTED)
        adapter.updateItemStatus(position,  "مرفوض")
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
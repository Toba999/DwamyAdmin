package com.dev.dwamyadmin.features.excuse.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev.dwamyadmin.databinding.FragmentExcuseBinding
import com.dev.dwamyadmin.domain.models.ExcuseRequest
import com.dev.dwamyadmin.domain.models.ExcuseStatus
import com.dev.dwamyadmin.features.excuse.presentation.viewModel.ExcuseViewModel
import com.dev.dwamyadmin.utils.SharedPrefManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ExcuseFragment : Fragment() {
    private val viewModel: ExcuseViewModel by viewModels()
    private var _binding: FragmentExcuseBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ExcuseAdapter

    @Inject
    lateinit var sharedPrefManager: SharedPrefManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExcuseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showLoading(true)
        setupRecyclerView()
        observeViewModel()
        val adminId = sharedPrefManager.getAdminId()
        viewModel.getExcuseRequests(adminId.toString())
    }

    private fun setupRecyclerView() {
        adapter = ExcuseAdapter(
            mutableListOf(),
            onAcceptClickListener = { excuseItem, position -> handleAcceptClick(excuseItem, position) },
            onDeclineClickListener = { excuseItem, position -> handleDeclineClick(excuseItem, position) }
        )

        binding.excuseRv.layoutManager = LinearLayoutManager(requireContext())
        binding.excuseRv.adapter = adapter
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.excuseRequests.collect { requests ->
                if (requests.isEmpty()) {
                    showLoading(false)
                    binding.emptyStateTv.visibility = View.VISIBLE
                    binding.emptyStateTv.text = "لا توجد طلبات أعذار"
                    binding.excuseRv.visibility = View.GONE
                } else {
                    showLoading(false)
                    binding.emptyStateTv.visibility = View.GONE
                    binding.excuseRv.visibility = View.VISIBLE
                    adapter.updateList(requests)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.updateStatusResult.collect { success ->
                success?.let {
                    showSnackBar(if (success) "تم تحديث الحالة بنجاح" else "فشل في تحديث الحالة", !success)
                }
            }
        }
    }
    private fun showLoading(isShown: Boolean) {
        binding.loadingView.root.isVisible = isShown
    }
    private fun handleAcceptClick(excuseItem: ExcuseRequest, position: Int) {
        viewModel.updateExcuseStatus(excuseItem.id, ExcuseStatus.ACCEPTED)
        adapter.updateItemStatus(position, ExcuseStatus.ACCEPTED)
    }

    private fun handleDeclineClick(excuseItem: ExcuseRequest, position: Int) {
        viewModel.updateExcuseStatus(excuseItem.id, ExcuseStatus.REJECTED)
        adapter.updateItemStatus(position, ExcuseStatus.REJECTED)
    }

    private fun showSnackBar(message: String, isError: Boolean) {
        val snackBar = Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG)
        snackBar.view.setBackgroundColor(resources.getColor(if (isError) android.R.color.holo_red_dark else android.R.color.holo_green_dark, null))
        snackBar.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

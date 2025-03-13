package com.dev.dwamyadmin.features.excuse.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev.dwamyadmin.databinding.FragmentExcuseBinding
import com.dev.dwamyadmin.features.vacation.presentation.view.VacationAdapter
import com.dev.dwamyadmin.features.vacation.presentation.view.VacationItem
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ExcuseFragment : Fragment() {

    private var _binding: FragmentExcuseBinding? = null
    private val binding get() = _binding!!

    private val excuseItems = mutableListOf(
        ExcuseItem(
            timestamp = "قبل 1 يوم",
            title = "طلب تأخير",
            fromTime = "10.30 ص",
            toTime = "11.30 ص",
            applicantName = "عبدالله محمد",
            status = "قيد المراجعة"
        ),
        ExcuseItem(
            timestamp = "قبل 2 يوم",
            title = "طلب إجازة",
            fromTime = "09.00 ص",
            toTime = "05.00 م",
            applicantName = "محمد أحمد",
            status = "قيد المراجعة"
        ))

    private lateinit var adapter:  ExcuseAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExcuseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the adapter
        adapter = ExcuseAdapter(
            excuseItems,
            onAcceptClickListener = { excuseItem, position ->
                // Handle accept button click
                handleAcceptClick(position)
            },
            onDeclineClickListener = { excuseItem, position ->
                // Handle decline button click
                handleDeclineClick(position)
            }
        )

        binding.emptyStateTv.text = ""
        // Set up the RecyclerView
        binding.excuseRv.layoutManager = LinearLayoutManager(requireContext())
        binding.excuseRv.adapter = adapter
    }

    private fun handleAcceptClick(position: Int) {
        // Update the status to "مقبول" (Accepted)
        adapter.updateItemStatus(position, "مقبول")
    }

    private fun handleDeclineClick(position: Int) {
        // Update the status to "مرفوض" (Declined)
        adapter.updateItemStatus(position, "مرفوض")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.dev.dwamyadmin.features.vacation.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev.dwamyadmin.databinding.FragmentVacationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VacationFragment : Fragment() {
    private var _binding: FragmentVacationBinding? = null
    private val binding get() = _binding!!

    private val vacationItems = mutableListOf(
        VacationItem(
            timestamp = "قبل 1 يوم",
            title = "طلب تأخير",
            fromTime = "10.30 ص",
            toTime = "11.30 ص",
            applicantName = "عبدالله محمد",
            status = "قيد المراجعة"
        ),
        VacationItem(
            timestamp = "قبل 2 يوم",
            title = "طلب إجازة",
            fromTime = "09.00 ص",
            toTime = "05.00 م",
            applicantName = "محمد أحمد",
            status = "قيد المراجعة"
        )
        // Add more items as needed
    )

    private lateinit var adapter: VacationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVacationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the adapter
        adapter = VacationAdapter(
            vacationItems,
            onAcceptClickListener = { vacationItem, position ->
                // Handle accept button click
                handleAcceptClick(position)
            },
            onDeclineClickListener = { vacationItem, position ->
                // Handle decline button click
                handleDeclineClick(position)
            }
        )

        binding.emptyStateTv.text = ""
        // Set up the RecyclerView
        binding.vacationRv.layoutManager = LinearLayoutManager(requireContext())
        binding.vacationRv.adapter = adapter
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
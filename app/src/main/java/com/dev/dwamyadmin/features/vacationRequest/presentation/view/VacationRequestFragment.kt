package com.dev.dwamyadmin.features.vacationRequest.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dev.dwamyadmin.R
import com.dev.dwamyadmin.databinding.FragmentVacationRequestBinding
import com.dev.dwamyadmin.features.vacationRequest.presentation.viewmodel.VacationRequestViewModel
import com.dev.dwamyadmin.utils.DateAndTimePicker


class VacationRequestFragment : Fragment() {
    private val viewModel: VacationRequestViewModel by viewModels()

    private var _binding: FragmentVacationRequestBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVacationRequestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
    }

    private fun setupUi(){
        binding.apply {
            vacationBackBtn.setOnClickListener{
                findNavController().popBackStack()
            }
            requestButton.setOnClickListener{
                findNavController().navigate(R.id.successDialogFragment)
            }
            startDate.setOnClickListener{
                DateAndTimePicker.showDatePicker(startDate,requireActivity())
            }
            EndDateEt.setOnClickListener{
                DateAndTimePicker.showDatePicker(EndDateEt,requireActivity())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
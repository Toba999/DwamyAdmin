package com.dev.dwamyadmin.features.excuseRequest.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dev.dwamyadmin.R
import com.dev.dwamyadmin.databinding.FragmentExcuseRequestBinding
import com.dev.dwamyadmin.features.excuseRequest.presentation.viewmodel.ExcuseRequestViewModel
import com.dev.dwamyadmin.utils.DateAndTimePicker
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ExcuseRequestFragment : Fragment() {
    private val viewModel: ExcuseRequestViewModel by viewModels()

    private var _binding: FragmentExcuseRequestBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExcuseRequestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
    }

    private fun setupUi(){
        binding.apply {
            excuseBackBtn.setOnClickListener{
                findNavController().popBackStack()
            }
            requestButton.setOnClickListener{
                findNavController().navigate(R.id.successDialogFragment)
            }
            dateEt.setOnClickListener{
                DateAndTimePicker.showDatePicker(dateEt,requireActivity())
            }
            startTimeEt.setOnClickListener{
                DateAndTimePicker.showTimePicker(startTimeEt,requireActivity())
            }
            endTimeEt.setOnClickListener{
                DateAndTimePicker.showTimePicker(endTimeEt,requireActivity())
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
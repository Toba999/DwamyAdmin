package com.dev.dwamyadmin.features.home.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dev.dwamyadmin.databinding.FragmentHomeBinding
import com.dev.dwamyadmin.features.home.presentation.viewModel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply{
            addEmpBtn.setOnClickListener {
                // to add employee fragment
                findNavController().navigate(HomeFragmentDirections.Companion.actionHomeFragmentToVacationRequestFragment())
            }

            deleteEmpBtn.setOnClickListener {
                // to delete employee fragment
                findNavController().navigate(HomeFragmentDirections.Companion.actionHomeFragmentToVacationRequestFragment())
            }
            reportsCard.setOnClickListener{
                // to reports fragment
              findNavController().navigate(HomeFragmentDirections.Companion.actionHomeFragmentToVacationRequestFragment())
            }
            execuseBtn.setOnClickListener{
                // to execuses requeset fragment
                findNavController().navigate(HomeFragmentDirections.Companion.actionHomeFragmentToExcuseRequestFragment())
            }
            vacBtn.setOnClickListener{
                // to vacation requeset fragment
                findNavController().navigate(HomeFragmentDirections.Companion.actionHomeFragmentToVacationRequestFragment())
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
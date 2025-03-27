package com.dev.dwamyadmin.features.home.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dev.dwamyadmin.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

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
                findNavController().navigate(HomeFragmentDirections.Companion.actionHomeFragmentToAddEmployeeFragment(false))
            }

            deleteEmpBtn.setOnClickListener {
                // to delete employee fragment
                findNavController().navigate(HomeFragmentDirections.Companion.actionHomeFragmentToEmployeeListFragment())
            }
            reportsCard.setOnClickListener{
                // to reports fragment
//              findNavController().navigate(HomeFragmentDirections.Companion.actionHomeFragmentToVacationRequestFragment())
            }
            execuseBtn.setOnClickListener{
                findNavController().navigate(HomeFragmentDirections.Companion.actionHomeFragmentToExcuseFragment())
            }
            vacBtn.setOnClickListener{
                findNavController().navigate(HomeFragmentDirections.Companion.actionHomeFragmentToVacationFragment())
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
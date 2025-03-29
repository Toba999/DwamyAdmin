package com.dev.dwamyadmin.features.home.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dev.dwamyadmin.R
import com.dev.dwamyadmin.databinding.FragmentHomeBinding
import com.dev.dwamyadmin.utils.SharedPrefManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    @Inject
    lateinit var sharedPrefManager: SharedPrefManager

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
            nameText.text = sharedPrefManager.getAdminName()
            addEmpBtn.setOnClickListener {
                findNavController().navigate(HomeFragmentDirections.Companion.actionHomeFragmentToAddEmployeeFragment(false))
            }

            deleteEmpBtn.setOnClickListener {
                findNavController().navigate(HomeFragmentDirections.Companion.actionHomeFragmentToEmployeeListFragment())
            }
            reportsCard.setOnClickListener{
              findNavController().navigate(HomeFragmentDirections.Companion.actionHomeFragmentToReportsFragment())
            }
            binding.execuseBtn.setOnClickListener {
                requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav_view)
                    .selectedItemId = R.id.excuseFragment
            }

            binding.vacBtn.setOnClickListener {
                requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav_view)
                    .selectedItemId = R.id.vacationFragment
            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
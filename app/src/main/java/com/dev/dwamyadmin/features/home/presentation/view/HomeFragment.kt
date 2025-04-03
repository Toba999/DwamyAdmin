package com.dev.dwamyadmin.features.home.presentation.view

import android.Manifest
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresPermission
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dev.dwamyadmin.R
import com.dev.dwamyadmin.databinding.FragmentHomeBinding
import com.dev.dwamyadmin.utils.SharedPrefManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    @Inject
    lateinit var sharedPrefManager: SharedPrefManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        if(sharedPrefManager.getCityName().isNullOrEmpty()) {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    val latitude = it.latitude
                    val longitude = it.longitude
                    val location = getTheCityName(latitude, longitude)
                    sharedPrefManager.setCityName(location ?: "")
                    binding.locationText.text = location
                }
            }
        }else{
            binding.locationText.text = sharedPrefManager.getCityName()
        }
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

    fun getTheCityName(lat: Double, lon: Double): String? {
        val geocoder = Geocoder(requireContext(), Locale("ar"))  // Arabic locale
        val addressList: List<Address>? = geocoder.getFromLocation(lat, lon, 1)

        return if (!addressList.isNullOrEmpty()) {
            addressList[0].locality ?: "المدينة غير متاحة"
        } else {
            "المدينة غير متاحة"
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
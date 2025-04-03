package com.dev.dwamyadmin.features.splash.presentation.view

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.dev.dwamyadmin.R
import com.dev.dwamyadmin.databinding.FragmentSplashBinding
import com.dev.dwamyadmin.features.splash.presentation.viewModel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SplashFragment : Fragment() {

    private val viewModel: SplashViewModel by viewModels()

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch{
            delay(1000)
        }
    }

    override fun onResume() {
        super.onResume()
        checkLocationPermissions()
    }
    private fun checkLocationPermissions() {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        when {
            shouldShowRequestPermissionRationale(permission) -> {
                showPermissionRationaleDialog()
            }
            else -> {
                requestPermissions(arrayOf(permission), LOCATION_PERMISSION_REQUEST_CODE)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkGPSAndNavigate()
            } else if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                showPermissionDeniedDialog()
            }
        }
    }
    private fun checkGPSAndNavigate() {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (!isGPSEnabled){
            showEnableGPSDialog()
        }else{
            navigateToLogin()
        }
    }
    fun navigateToLogin(){
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.SplashFragment, true)
            .setLaunchSingleTop(true)
            .build()

        findNavController().navigate(R.id.LoginFragment, null, navOptions)
    }
    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("إذن الموقع مطلوب")
            .setMessage("يرجى منح إذن الموقع لاختيار الموقع.")
            .setPositiveButton("حسنًا") { _, _ ->
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            }
            .setNegativeButton("إلغاء", null)
            .show()
    }
    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("إذن الموقع مرفوض")
            .setMessage("لا يمكن اختيار الموقع بدون الإذن. يرجى منحه من الإعدادات.")
            .setPositiveButton("فتح الإعدادات") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:" + requireContext().packageName)
                startActivity(intent)
            }
            .setNegativeButton("إلغاء", null)
            .show()
    }

    private fun showEnableGPSDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("تشغيل الـ GPS")
            .setMessage("يرجى تفعيل الـ GPS لاختيار الموقع.")
            .setPositiveButton("فتح الإعدادات") { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("إلغاء", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object {
        internal const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}
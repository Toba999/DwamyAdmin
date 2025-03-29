package com.dev.dwamyadmin.features.mapScreen

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dev.dwamyadmin.R
import com.dev.dwamyadmin.databinding.FragmentMapBinding
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale
import kotlin.collections.isNullOrEmpty

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private var googleMap: GoogleMap? = null
    private var selectedLocation: LatLng? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)
        binding.selectLocationButton.setOnClickListener {
            selectedLocation?.let {
                returnSelectedLocation(it)
            } ?: Toast.makeText(requireContext(), "من فضلك اختر مكان العمل", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap?.isMyLocationEnabled = true
            moveToCurrentLocation()
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }

        googleMap?.setOnMapClickListener { latLng ->
            googleMap?.clear()
            googleMap?.addMarker(MarkerOptions().position(latLng).title("المكان المحدد"))
            selectedLocation = latLng
        }
    }

    private fun moveToCurrentLocation() {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return
        }

        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                }
            }
        }
    }

    private fun returnSelectedLocation(latLng: LatLng) {
        val geocoder = Geocoder(requireContext(), Locale("ar"))  // Arabic locale
        val addressList: List<Address>? = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

        val address = if (!addressList.isNullOrEmpty()) {
            addressList[0].getAddressLine(0)  // Get full address in Arabic
        } else {
            "العنوان غير متاح"
        }

        parentFragmentManager.setFragmentResult(
            "locationRequestKey",
            Bundle().apply {
                putDouble("latitude", latLng.latitude)
                putDouble("longitude", latLng.longitude)
                putString("address", address)  // Send Arabic address
            }
        )
        parentFragmentManager.popBackStack()
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
        }
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


    override fun onResume() {
        super.onResume()
        checkLocationPermissions()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView.onDestroy()
        _binding = null
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    companion object {
        internal const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}

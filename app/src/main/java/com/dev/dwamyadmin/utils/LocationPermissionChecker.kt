package com.dev.dwamyadmin.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.Manifest
import android.R
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

class LocationPermissionChecker(private val context: Context) {

    fun isPermissionGranted(): Boolean {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission(activity: Activity, requestCode: Int) {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            Snackbar.make(
                activity.findViewById(R.id.content),
                "Location permission is required for this feature.",
                Snackbar.LENGTH_INDEFINITE
            ).setAction("Grant") {
                ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
            }.show()
        } else {
            ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
        }
    }

    fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    fun navigateToSettings(activity: Activity) {
        Snackbar.make(
            activity.findViewById(R.id.content),
            "Location permission is required. Go to settings to enable it.",
            Snackbar.LENGTH_LONG
        ).setAction("Settings") {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
            context.startActivity(intent)
        }.show()
    }
}

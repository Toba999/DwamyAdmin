package com.dev.dwamyadmin.data.geofence

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class GeoFenceChecker(
    private val centerLat: Double,
    private val centerLng: Double,
    private val radius: Double
) {

    private fun calculateDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val earthRadius = 6371e3 // Earth radius in meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)

        val a =
            sin(dLat / 2) * sin(dLat / 2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(
                dLng / 2
            ) * sin(dLng / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }

    fun isInside(lat: Double, lng: Double): Boolean {
        val distance = calculateDistance(centerLat, centerLng, lat, lng)
        return distance <= radius
    }
}
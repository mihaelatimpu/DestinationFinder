package com.mimi.destinationfinder.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import com.mimi.destinationfinder.dto.Destination
import com.mimi.destinationfinder.dto.Location

/**
 * Created by Mimi on 18/11/2017.
 *
 */
class CurrentLocationUtil {
    @SuppressLint("MissingPermission")
    fun findLocation(context: Context, provider: String): Destination? {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isEnabled = locationManager.isProviderEnabled(provider)
        return if (isEnabled) {
            val androidLocation = locationManager.getLastKnownLocation(provider)
            if (androidLocation != null)
                GeocoderUtil().getAddress(context,
                        Location(androidLocation.latitude, androidLocation.longitude))
            else
                null
        } else
            null
    }
}

package com.mimi.destinationfinder.utils

import android.content.Context
import android.location.Geocoder
import com.mimi.destinationfinder.dto.Destination
import com.mimi.destinationfinder.dto.Location

/**
 * Created by Mimi on 18/11/2017.
 *
 */
class GeocoderUtil {
    fun getAddress(context: Context, location: Location): Destination? {
        val geocode = Geocoder(context)
        val locations = geocode.getFromLocation(location.lat, location.lng, 1)
        if (locations.isEmpty())
            return null
        val address = locations.first()
        return Destination(
                location = location,
                errorMargin = 0.0,
                zipCode = address.postalCode,
                street = address.thoroughfare,
                cityName = address.locality
        )
    }
}
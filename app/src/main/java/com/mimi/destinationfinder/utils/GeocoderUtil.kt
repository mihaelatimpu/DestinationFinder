package com.mimi.destinationfinder.utils

import android.content.Context
import android.location.Geocoder
import com.mimi.destinationfinder.dto.Location
import com.mimi.destinationfinder.dto.Coordinates

/**
 * Created by Mimi on 18/11/2017.
 *
 */
class GeocoderUtil {
    fun getAddress(context: Context, coordinates: Coordinates): Location? {
        try {
            val geocode = Geocoder(context)
            val locations = geocode.getFromLocation(coordinates.lat, coordinates.lng, 1)
            if (locations.isEmpty())
                return null
            val address = locations.first()
            return Location(
                    coordinates = coordinates,
                    errorMargin = 0.0,
                    zipCode = address.postalCode,
                    street = address.thoroughfare,
                    cityName = address.locality
            )
        } catch (e:Exception){
            e.printStackTrace()
            return Location(coordinates = coordinates, cityName = coordinates.toString(), errorMargin = 100.0)
        }
    }
}
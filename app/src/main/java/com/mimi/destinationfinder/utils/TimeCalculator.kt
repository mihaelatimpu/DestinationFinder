package com.mimi.destinationfinder.utils

import com.mimi.destinationfinder.dto.Location
import com.mimi.destinationfinder.dto.TransportMean

/**
 * Created by Mimi on 17/11/2017.
 * This class calculates the time required to get from one location to another
 * based on the distance and the means to get there (walking, car, bus, etc.)
 */
class TimeCalculator{
    fun calculateTime(from: Location, to: Location, mean:TransportMean = TransportMean()):Double{
        val results = FloatArray(1)
        android.location.Location.distanceBetween(from.lat,from.lng, to.lat, to.lng,
                results)
        val distance = results[0]
        val speed = mean.getSpeedPerMinute()
        return distance / speed
    }
}

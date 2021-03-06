package com.mimi.destinationfinder.utils

import com.mimi.destinationfinder.dto.Coordinates
import com.mimi.destinationfinder.dto.TransportMode

/**
 * Created by Mimi on 17/11/2017.
 * This class calculates the time required to get from one coordinates to another
 * based on the distance and the means to get there (walking, car, bus, etc.)
 */
class TimeCalculator{
    fun calculateTime(from: Coordinates, to: Coordinates, mean: TransportMode):Double{
        val results = FloatArray(1)
        android.location.Location.distanceBetween(from.lat,from.lng, to.lat, to.lng,
                results)
        val distance = results[0]
        val speed = mean.getSpeedPerMinute()
        return distance / speed
    }
}

package com.mimi.destinationfinder.dto

/**
 * Created by Mimi on 17/11/2017.
 *
 */
class TransportMode( val type: Int) {
    companion object {
        val TYPE_DRIVING = 0
        val TYPE_WALKING = 1
        val TYPE_CYCLING = 2
        fun default() = TransportMode(TYPE_WALKING)
    }

    /**
     * get the approximate speed as meters per minute based on mean
     */
    fun getSpeedPerMinute(): Double {
        val kmPerHour = when (type) {
            TYPE_WALKING -> 5
            TYPE_DRIVING -> 50
            TYPE_CYCLING -> 15
            else -> throw UnsupportedOperationException("Unkown type: $type")
        }
        val meterPerSecond = kmPerHour * 0.277778
        return meterPerSecond * 60
    }
    fun getGoogleApiTravelMode()
            = when (type) {
        TYPE_WALKING -> "walking"
        TYPE_DRIVING -> "driving"
        TYPE_CYCLING -> "bicycling"
        else -> throw UnsupportedOperationException("Unkown type: $type")
    }
}
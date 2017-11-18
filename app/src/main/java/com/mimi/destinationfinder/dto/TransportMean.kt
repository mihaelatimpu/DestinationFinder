package com.mimi.destinationfinder.dto

/**
 * Created by Mimi on 17/11/2017.
 *
 */
class TransportMean(private val type:Int = TYPE_WALKING){
    companion object {
        val TYPE_WALKING = 0
        val TYPE_DRIVING = 1
    }

    /**
     * get the approximate speed as meters per minute based on mean
     */
    fun getSpeedPerMinute():Double{
      val kmPerHour  = when(type){
            TYPE_WALKING -> 5
            TYPE_DRIVING -> 50
            else -> throw UnsupportedOperationException("Unkown type: $type")
        }
        val meterPerSecond = kmPerHour * 0.277778
        return meterPerSecond * 60
    }
}
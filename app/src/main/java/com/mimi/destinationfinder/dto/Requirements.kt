package com.mimi.destinationfinder.dto

import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Mimi on 18/11/2017.
 * The required information in order to find the coordinates
 */
class Requirements {
    companion object {
        fun initialRequirements(): Requirements {
            val rq = Requirements()
            rq.arrivalTime.add(Calendar.HOUR, 1)
            return rq
        }
    }

    lateinit var initialCoordinates: Coordinates
    val departureTime: Calendar = Calendar.getInstance()
    val arrivalTime: Calendar = Calendar.getInstance()
    var settings: Settings = Settings.default()

    private fun getTravelTimeInMillis() = arrivalTime.timeInMillis - departureTime.timeInMillis
    fun givenTravelTime() = TimeUnit.MILLISECONDS.toMinutes(getTravelTimeInMillis())
    fun getSearchRadius() = settings.radius.getMaxRadiusInMeters(this)
}

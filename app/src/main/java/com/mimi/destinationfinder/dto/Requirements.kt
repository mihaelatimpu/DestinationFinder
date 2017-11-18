package com.mimi.destinationfinder.dto

import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Mimi on 18/11/2017.
 * The necessary information in order to find the location
 */
class Requirements {
    lateinit var initialLocation: Location
    val departureTime: Calendar = Calendar.getInstance()
    val arrivalTime: Calendar = Calendar.getInstance()
    val radius: Int = 500
    var types: List<String> = listOf(
            "airport", "hospital",
            "amusement_park", "aquarium",
            "bank", "bar", "gym",
            "meal_takeaway", "cafe",
            "dentist", "university")
    val transportMean: TransportMean = TransportMean(TransportMean.TYPE_WALKING)
    val maxResults: Long = 10

    private fun getTravelTimeInMillis() = arrivalTime.timeInMillis - departureTime.timeInMillis
    fun getTravelTimeInMinutes() = TimeUnit.MILLISECONDS.toMinutes(getTravelTimeInMillis())
}

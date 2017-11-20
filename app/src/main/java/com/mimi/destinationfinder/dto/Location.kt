package com.mimi.destinationfinder.dto

import com.mimi.destinationfinder.utils.TimeConverter
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Mimi on 17/11/2017.
 *
 */
class Location(
        val coordinates: Coordinates? = null,
        val street: String? = null,
        val zipCode: String? = null,
        val cityName: String,
        var errorMargin: Double,
        var arrivalTime: Long? = null) {

    override fun toString() = if (coordinates == null)
        cityName else "$street, $zipCode $cityName"

    fun calculateArrivalTime(leavingTime: Calendar,
                             travelTimeInSeconds: Long) {
        arrivalTime = leavingTime.timeInMillis +
                TimeUnit.SECONDS.toMillis(travelTimeInSeconds)
    }

    fun toStringWithArrivalTime(): String {
        if (arrivalTime != null) {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = arrivalTime!!
            val arrivalTimeString = TimeConverter().toRegularTimeString(calendar)
            return "${toString()} ($arrivalTimeString)"
        }
        return toString()
    }
}

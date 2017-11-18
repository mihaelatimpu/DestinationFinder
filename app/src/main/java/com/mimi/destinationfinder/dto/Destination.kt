package com.mimi.destinationfinder.dto

/**
 * Created by Mimi on 17/11/2017.
 *
 */
class Destination(
        val location: Location? = null,
        val street: String? = null,
        val zipCode: String? = null,
        val cityName: String,
        val errorMargin: Double) {
    override fun toString() = if (location == null) cityName else "$street, $zipCode $cityName"
}

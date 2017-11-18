package com.mimi.destinationfinder.dto

/**
 * Created by Mimi on 17/11/2017.
 *
 */
class Address(val placeId: String,
              val location: Location,
              val street: String,
              val zipCode: String,
              val cityName: String,
              val errorMargin: Double) {
    override fun toString() = "$street, $zipCode $cityName"
}

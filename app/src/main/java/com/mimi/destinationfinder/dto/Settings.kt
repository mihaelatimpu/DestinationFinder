package com.mimi.destinationfinder.dto

/**
 * Created by Mimi on 19/11/2017.
 *
 */
class Settings(var radius: Radius, var transportMode: TransportMode, var maxResultPerCount: ResultsCount,
               var placesOnInterests: List<String>) {
    companion object {
        fun default()
                = Settings(radius = Radius.default(), transportMode = TransportMode.default(),
                maxResultPerCount = ResultsCount.default(), placesOnInterests = listOf(
                "airport", "hospital", "bar", "gym"
                /*,"amusement_park", "aquarium",
                "bank",
                "meal_takeaway", "cafe",
                "dentist", "university"*/))
    }
}

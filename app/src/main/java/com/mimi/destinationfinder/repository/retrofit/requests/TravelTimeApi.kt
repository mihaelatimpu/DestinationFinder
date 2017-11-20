package com.mimi.destinationfinder.repository.retrofit.requests

import com.mimi.destinationfinder.dto.Location
import com.mimi.destinationfinder.dto.Requirements
import com.mimi.destinationfinder.repository.retrofit.ApiClient
import com.mimi.destinationfinder.repository.retrofit.RetrofitInterface
import com.mimi.destinationfinder.repository.retrofit.responses.TravelTimeResponse
import io.reactivex.Observable


/**
 * Created by Mimi on 19/11/2017.
 * This request will calculate the travel time using google maps api,
 * based on transport mode and departure time
 */
class TravelTimeApi(val data: Requirements, private val locations: List<Location>) {
    companion object {
        private val API_KEY = "AIzaSyBtmN1jViCJwNmx25_BmIisTIv1HJ7OPq8"
    }

    fun start(): Observable<Location> =
            collectObservables(ApiClient.googleApi)

    private fun collectObservables(api: RetrofitInterface)
            : Observable<Location> {

        val observableList = locations.map {
            getResponseAndDestination(api, it)
        }
        return Observable.mergeDelayError(observableList)
    }


    private fun getResponseAndDestination(api: RetrofitInterface, location: Location): Observable<Location> {
        return api.getTravelTime(apiKey = API_KEY,
                origin = data.initialCoordinates.toString(),
                destination = location.coordinates.toString(),
                travelMode = data.settings.transportMode.getGoogleApiTravelMode(),
                units = "metric").map { mergeResponseAndDestination(it, location) }
    }

    private fun mergeResponseAndDestination(response: TravelTimeResponse, location: Location): Location? {
        if (response.routes == null ||
                response.routes.isEmpty() || response.routes.first().legs.isEmpty())
            return null
        val travelTimeInSeconds = response.routes.first().legs.first().duration.value
        val travelTimeInMinutes = travelTimeInSeconds / 60
        val errorMargin = Math.abs(data.givenTravelTime() - travelTimeInMinutes).toDouble()
        location.errorMargin = errorMargin
        location.calculateArrivalTime(data.departureTime,travelTimeInSeconds)
        return location

    }

}
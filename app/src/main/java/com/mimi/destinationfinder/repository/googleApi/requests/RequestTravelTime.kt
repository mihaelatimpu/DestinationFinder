package com.mimi.destinationfinder.repository.googleApi.requests

import com.mimi.destinationfinder.dto.Destination
import com.mimi.destinationfinder.dto.Requirements
import com.mimi.destinationfinder.repository.googleApi.ApiClient
import com.mimi.destinationfinder.repository.googleApi.RetrofitInterface
import com.mimi.destinationfinder.repository.googleApi.responses.TravelTimeResponse
import io.reactivex.Observable


/**
 * Created by Mimi on 19/11/2017.
 * This request will calculate the travel time using google maps api,
 * based on transport mode and departure time
 */
class RequestTravelTime(val data: Requirements, private val destinations: List<Destination>) {
    companion object {
        private val API_KEY = "AIzaSyBtmN1jViCJwNmx25_BmIisTIv1HJ7OPq8"
    }

    fun start(): Observable<Destination> =
            collectObservables(ApiClient.googleApi)

    private fun collectObservables(api: RetrofitInterface)
            : Observable<Destination> {

        val observableList = destinations.map {
            getResponseAndDestination(api, it)
        }
        return Observable.mergeDelayError(observableList)
    }


    private fun getResponseAndDestination(api: RetrofitInterface, destination: Destination): Observable<Destination> {
        return api.getTravelTime(apiKey = API_KEY,
                origin = data.initialLocation.toString(),
                destination = destination.location.toString(),
                travelMode = data.settings.transportMode.getGoogleApiTravelMode(),
                units = "metric").map { mergeResponseAndDestination(it, destination) }
    }

    private fun mergeResponseAndDestination(response: TravelTimeResponse, destination: Destination): Destination? {
        if (response.routes == null ||
                response.routes.isEmpty() || response.routes.first().legs.isEmpty())
            return null
        val travelTimeInSeconds = response.routes.first().legs.first().duration.value
        val travelTimeInMinutes = travelTimeInSeconds / 60
        val errorMargin = Math.abs(data.givenTravelTime() - travelTimeInMinutes).toDouble()
        destination.errorMargin = errorMargin
        destination.calculateArrivalTime(data.departureTime,travelTimeInSeconds)
        return destination

    }

}
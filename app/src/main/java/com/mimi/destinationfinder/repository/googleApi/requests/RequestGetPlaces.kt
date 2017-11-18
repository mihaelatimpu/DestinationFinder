package com.mimi.destinationfinder.repository.googleApi.requests

import android.content.Context
import com.mimi.destinationfinder.dto.GooglePlace
import com.mimi.destinationfinder.dto.Requirements
import com.mimi.destinationfinder.repository.googleApi.ApiClient
import com.mimi.destinationfinder.repository.googleApi.RetrofitInterface
import com.mimi.destinationfinder.repository.googleApi.responses.GetPlacesNearbyResponse
import io.reactivex.Observable

/**
 * Created by Mimi on 17/11/2017.
 *
 */
class RequestGetPlaces(val data: Requirements) {

    fun start(context: Context): Observable<GooglePlace> {
        val api = ApiClient.googleApi
        val apiKey = ApiClient.getApiKey(context)

        val observable = collectObservablesFromApi(api, apiKey)
        return mapToGooglePlaces(observable)
    }

    private fun collectObservablesFromApi(api: RetrofitInterface, apiKey: String)
            : Observable<GetPlacesNearbyResponse> {
        val observableList = data.types.map {
            api.getPlacesNearby(apiKey,
                    data.initialLocation.toString(), data.radius, it)
        }
        return Observable.mergeDelayError(observableList)

    }

    private fun mapToGooglePlaces(observable: Observable<GetPlacesNearbyResponse>
    ): Observable<GooglePlace> {
        return observable.filter { it.status == "OK" }
                .map { it.results }
                .flatMapIterable { x -> x }
    }

}
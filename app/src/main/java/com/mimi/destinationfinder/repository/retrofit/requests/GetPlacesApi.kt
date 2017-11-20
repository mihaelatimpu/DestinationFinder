package com.mimi.destinationfinder.repository.retrofit.requests

import android.content.Context
import com.mimi.destinationfinder.dto.GooglePlace
import com.mimi.destinationfinder.dto.Requirements
import com.mimi.destinationfinder.repository.retrofit.ApiClient
import com.mimi.destinationfinder.repository.retrofit.RetrofitInterface
import com.mimi.destinationfinder.repository.retrofit.responses.GetPlacesNearbyResponse
import io.reactivex.Observable

/**
 * Created by Mimi on 17/11/2017.
 *
 */
class GetPlacesApi(val data: Requirements) {

    fun start(context: Context): Observable<GooglePlace> {
        val api = ApiClient.googleApi
        val apiKey = ApiClient.getApiKey(context)

        val observable = collectObservablesFromApi(api, apiKey)
        return mapToGooglePlaces(observable)
    }

    private fun collectObservablesFromApi(api: RetrofitInterface, apiKey: String)
            : Observable<GetPlacesNearbyResponse> {
        val observableList = data.settings.placesOnInterests.map {
            api.getPlacesNearby(apiKey,
                    data.initialCoordinates.toString(), data.getSearchRadius(), it)
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
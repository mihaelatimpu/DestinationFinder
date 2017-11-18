package com.mimi.destinationfinder.repository.retrofit.requests

import com.mimi.destinationfinder.dto.GooglePlace
import com.mimi.destinationfinder.dto.Location
import com.mimi.destinationfinder.repository.retrofit.RetrofitInterface
import com.mimi.destinationfinder.repository.retrofit.responses.GetPlacesNearbyResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Mimi on 17/11/2017.
 *
 */
class RequestGetPlacesByType(private val location: Location, private val radius: Int,
                             private val onCompleted: (List<GooglePlace>) -> Unit,
                             private val type: String,
                             private val onError: (String?) -> Unit) : BaseRequest<GetPlacesNearbyResponse>() {


    override fun call(api: RetrofitInterface, apiKey: String) =
            api.getPlacesNearby(apiKey = apiKey, location = location.toString(),
                    radius = radius, type = type)

    override fun onError(t: Throwable?) {
        onError(t?.localizedMessage ?: "failure")
    }

    override fun onCompleted(result: GetPlacesNearbyResponse) {
        onCompleted(result.results)
    }

}

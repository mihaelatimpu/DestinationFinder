package com.mimi.destinationfinder.repository.googleApi

import com.mimi.destinationfinder.repository.googleApi.responses.GetPlaceAddressResponse
import com.mimi.destinationfinder.repository.googleApi.responses.GetPlacesNearbyResponse
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by Mimi on 17/11/2017.
 *
 */
interface RetrofitInterface {

    @GET("/maps/api/place/radarsearch/json")
    fun getPlacesNearby(@Query("key") apiKey: String, @Query("location") location: String,
                        @Query("radius") radius: Int, @Query("type") type: String)
            : Observable<GetPlacesNearbyResponse>

    @GET("/maps/api/place/details/json")
    fun getPlaceAddress(@Query("key") apiKey: String, @Query("placeid") placeId: String)
            : Observable<GetPlaceAddressResponse>
}

package com.mimi.destinationfinder.repository.googleApi

import com.mimi.destinationfinder.repository.googleApi.responses.GetPlacesNearbyResponse
import com.mimi.destinationfinder.repository.googleApi.responses.TravelTimeResponse
import io.reactivex.Observable
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

    @GET("/maps/api/directions/json")
    fun getTravelTime(@Query("key") apiKey: String,
                      @Query("origin") origin: String,
                      @Query("destination") destination: String,
                      @Query("mode") travelMode: String,
                      @Query("units") units: String)
            : Observable<TravelTimeResponse>

}

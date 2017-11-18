package com.mimi.destinationfinder.repository.retrofit

import com.mimi.destinationfinder.repository.retrofit.responses.GetPlaceAddressResponse
import com.mimi.destinationfinder.repository.retrofit.responses.GetPlacesNearbyResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by Mimi on 17/11/2017.
 *
 */
public interface RetrofitInterface {

    @GET("/maps/api/place/radarsearch/json")
    fun getPlacesNearby(@Query("key") apiKey: String, @Query("location") location: String,
                        @Query("radius") radius: Int, @Query("type") type: String)
            : Call<GetPlacesNearbyResponse>

    @GET("/maps/api/place/details/json")
    fun getPlaceAddress(@Query("key") apiKey: String, @Query("placeid") placeId: String)
            : Call<GetPlaceAddressResponse>
}

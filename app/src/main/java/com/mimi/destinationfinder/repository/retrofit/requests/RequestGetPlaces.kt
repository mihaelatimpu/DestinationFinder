package com.mimi.destinationfinder.repository.retrofit.requests

import android.content.Context
import android.content.pm.PackageManager
import com.mimi.destinationfinder.dto.GooglePlace
import com.mimi.destinationfinder.dto.Location
import com.mimi.destinationfinder.repository.retrofit.ApiClient
import com.mimi.destinationfinder.repository.retrofit.RetrofitInterface

/**
 * Created by Mimi on 17/11/2017.
 *
 */
class RequestGetPlaces(val location: Location, private val radius: Int,
                       private val types: List<String>) {
    private val remainingTypes = ArrayList<String>()
    private val places = ArrayList<GooglePlace>()
    private var onCompleted: (List<GooglePlace>) -> Unit = {}

    fun start(context: Context, onCompleted: (List<GooglePlace>) -> Unit) {
        val api = ApiClient.googleApi
        val apiKey = ApiClient.getApiKey(context)

        this.onCompleted = onCompleted
        remainingTypes.addAll(types)
        types.forEach {
            getPlacesForType(api, apiKey, it)
        }
    }

    private fun getPlacesForType(api: RetrofitInterface, apiKey: String, type: String) {
        RequestGetPlacesByType(location = location,
                radius = radius, onCompleted = {
            onCompleted(list = it, type = type)
        }, type = type, onError = {
            onError(type)
        }).start(api, apiKey)

    }

    private fun onCompleted(type: String, list: List<GooglePlace>) {
        remainingTypes.remove(type)
        places.addAll(list)
        if (remainingTypes.size == 0)
            onCompleted(places)
    }

    private fun onError(type: String) {
        remainingTypes.remove(type)
        if (remainingTypes.size == 0)
            onCompleted(places)
    }
}
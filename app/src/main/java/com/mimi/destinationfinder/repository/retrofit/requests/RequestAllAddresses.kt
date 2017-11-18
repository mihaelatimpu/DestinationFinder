package com.mimi.destinationfinder.repository.retrofit.requests

import android.content.Context
import com.mimi.destinationfinder.dto.Address
import com.mimi.destinationfinder.dto.GooglePlace
import com.mimi.destinationfinder.repository.retrofit.ApiClient
import com.mimi.destinationfinder.repository.retrofit.RetrofitInterface

/**
 * Created by Mimi on 17/11/2017.
 *
 */
class RequestAllAddresses(private val list: List<GooglePlace>) {
    private val remainingPlaces = ArrayList<GooglePlace>()
    private val addresses = ArrayList<Address>()
    private var onCompleted: (List<Address>) -> Unit = {}

    fun start(context: Context, onCompleted: (List<Address>) -> Unit) {
        this.onCompleted = onCompleted
        this.remainingPlaces.addAll(list)
        val api = ApiClient.googleApi
        val apiKey = ApiClient.getApiKey(context)
        list.forEach {
            getAddress(api = api, apiKey = apiKey, place = it)
        }
    }

    private fun getAddress(api: RetrofitInterface, apiKey: String, place: GooglePlace) {
        RequestAddressByPlaceId(
                place = place,
                onError = {
                    remainingPlaces.remove(place)
                    saveComplete()
                },
                onCompleted = {
                    remainingPlaces.remove(place)
                    addresses.add(it)
                    saveComplete()
                })
                .start(api, apiKey)
    }

    private fun saveComplete() {
        if (remainingPlaces.isEmpty())
            onCompleted(addresses)
    }
}
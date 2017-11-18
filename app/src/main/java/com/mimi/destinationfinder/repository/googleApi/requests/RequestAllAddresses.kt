package com.mimi.destinationfinder.repository.googleApi.requests

import android.content.Context
import com.mimi.destinationfinder.dto.Destination
import com.mimi.destinationfinder.dto.GooglePlace
import com.mimi.destinationfinder.repository.googleApi.ApiClient
import com.mimi.destinationfinder.repository.googleApi.RetrofitInterface
import com.mimi.destinationfinder.repository.googleApi.responses.GetPlaceAddressResponse
import com.mimi.destinationfinder.repository.googleApi.responses.Result
import io.reactivex.Observable

/**
 * Created by Mimi on 17/11/2017.
 *
 */
class RequestAllAddresses(private val list: List<GooglePlace>) {

    private val converter = AddressConverted()

    fun start(context: Context): Observable<Destination> {
        val api = ApiClient.googleApi
        val apiKey = ApiClient.getApiKey(context)

        val observable = collectObservables(api, apiKey)
        return mapToDestinations(observable)
    }

    private fun collectObservables(api: RetrofitInterface, apiKey: String)
            : Observable<GetPlaceAddressResponse> {
        val observableList = list.map { api.getPlaceAddress(apiKey, it.place_id) }
        return Observable.mergeDelayError(observableList)

    }

    private fun mapToDestinations(observable: Observable<GetPlaceAddressResponse>): Observable<Destination> {
        return observable.filter { it.status == "OK" && it.result != null }
                .map {
                    val placeId = it.result!!.place_id
                    val place = list.firstOrNull { it.place_id == placeId }
                    if (place != null)
                        converter.convertToAddress(it.result, place)
                    else
                        null
                }

    }

    class AddressConverted {
        companion object {
            val STREET = "street_address"
            val ROUTE = "route"
            val ZIP_CODE = "postal_code"
            val CITY = "locality"
        }

        fun convertToAddress(response: Result, place: GooglePlace): Destination {
            val location = place.geometry.location
            val streetName = findAddressComponent(response, STREET, ROUTE)
            val zipCode = findAddressComponent(response, ZIP_CODE)
            val city = findAddressComponent(response, CITY)
            val errorMargin = place.errorMargin
            return Destination(location = location, street = streetName,
                    zipCode = zipCode, cityName = city, errorMargin = errorMargin)
        }

        private fun findAddressComponent(response: Result, vararg components: String): String {
            response.address_components.forEach {
                val types = it.types
                val name = it.long_name
                components.forEach {
                    if (types.contains(it))
                        return name
                }
            }
            return ""
        }
    }
}
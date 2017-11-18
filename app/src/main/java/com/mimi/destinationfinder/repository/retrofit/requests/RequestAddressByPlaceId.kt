package com.mimi.destinationfinder.repository.retrofit.requests

import com.mimi.destinationfinder.dto.Address
import com.mimi.destinationfinder.dto.GooglePlace
import com.mimi.destinationfinder.repository.retrofit.RetrofitInterface
import com.mimi.destinationfinder.repository.retrofit.responses.GetPlaceAddressResponse

/**
 * Created by Mimi on 17/11/2017.
 *
 */
class RequestAddressByPlaceId(private val place: GooglePlace,
                              private val onCompleted: (Address) -> Unit,
                              private val onError: (String?) -> Unit)
    : BaseRequest<GetPlaceAddressResponse>() {
    override fun call(api: RetrofitInterface, apiKey: String)
            = api.getPlaceAddress(apiKey, placeId = place.place_id)

    override fun onError(t: Throwable?) {
        onError(t?.message)
    }

    override fun onCompleted(result: GetPlaceAddressResponse) {
        onCompleted(AddressConverted().convertToAddress(result, place))
    }

}

class AddressConverted {
    companion object {
        val STREET = "street_address"
        val ROUTE = "route"
        val ZIP_CODE = "postal_code"
        val CITY = "locality"
    }

    fun convertToAddress(response: GetPlaceAddressResponse, place: GooglePlace): Address {
        val id = place.place_id
        val location = place.geometry.location
        val streetName = findAddressComponent(response, STREET, ROUTE)
        val zipCode = findAddressComponent(response, ZIP_CODE)
        val city = findAddressComponent(response, CITY)
        val errorMargin = place.errorMargin
        return Address(placeId = id, location = location, street = streetName,
                zipCode = zipCode, cityName = city, errorMargin = errorMargin)
    }

    private fun findAddressComponent(response: GetPlaceAddressResponse, vararg components: String): String {
        response.result.address_components.forEach {
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
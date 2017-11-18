package com.mimi.destinationfinder.repository.googleApi.responses

/**
 * Created by Mimi on 17/11/2017.
 *
 */
class GetPlaceAddressResponse(status: String = "OK",
                              val result: Result? = null) : BaseResponse(status)

class Result(val address_components: List<AddressComponents> = arrayListOf(), val place_id: String)
class AddressComponents(val long_name: String, val shortName: String, val types: List<String>)
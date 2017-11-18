package com.mimi.destinationfinder.repository.retrofit.responses

/**
 * Created by Mimi on 17/11/2017.
 *
 */
class GetPlaceAddressResponse(status:String,
                              val result:Result):BaseResponse(status)

class Result(val address_components:List<AddressComponents>)
class AddressComponents(val long_name:String, val shortName:String, val types:List<String>)
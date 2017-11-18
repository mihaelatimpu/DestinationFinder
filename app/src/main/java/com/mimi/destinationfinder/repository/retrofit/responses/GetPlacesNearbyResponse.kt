package com.mimi.destinationfinder.repository.retrofit.responses

import com.mimi.destinationfinder.dto.GooglePlace

/**
 * Created by Mimi on 17/11/2017.
 *
 */
class GetPlacesNearbyResponse(status:String,
                              val error_message:String,
                              val results:List<GooglePlace>):BaseResponse(status)
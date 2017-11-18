package com.mimi.destinationfinder.repository.googleApi.responses

import com.mimi.destinationfinder.dto.GooglePlace

/**
 * Created by Mimi on 17/11/2017.
 *
 */
class GetPlacesNearbyResponse(status: String = "OK",
                              val results: List<GooglePlace> = listOf()) : BaseResponse(status)
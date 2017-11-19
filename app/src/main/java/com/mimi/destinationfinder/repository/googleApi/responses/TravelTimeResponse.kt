package com.mimi.destinationfinder.repository.googleApi.responses

/**
 * Created by Mimi on 17/11/2017.
 *
 */
class TravelTimeResponse(status: String = "OK",
                         val routes: List<Route>?,
                         val error_message:String?) : BaseResponse(status)

class Route(val legs: List<Leg>)
class Leg(val duration: Duration, val end_location: EndLocation)
class Duration(val text: String, val value: Long)
class EndLocation(val lat:Double, val lng:Double)
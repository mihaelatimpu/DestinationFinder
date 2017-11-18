package com.mimi.destinationfinder.dto

/**
 * Created by Mimi on 17/11/2017.
 *
 */
class GooglePlace(val geometry: Geometry, val place_id: String) {
    var errorMargin = 0.0
}

class Geometry(val location: Location)
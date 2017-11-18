package com.mimi.destinationfinder.dto

import com.google.android.gms.maps.model.LatLng

/**
 * Created by Mimi on 17/11/2017.
 *
 */

class Location(val lat: Double, val lng: Double) {
    constructor(latLng:LatLng):this(lat = latLng.latitude, lng = latLng.longitude)
    override fun toString() = "$lat,$lng"
}

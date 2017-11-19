package com.mimi.destinationfinder.dto

/**
 * Created by Mimi on 19/11/2017.
 * The max radius for searching locations
 */
class Radius(val type: Int) {
    companion object {
        val METER_200 = 0
        val METER_500 = 1
        val KM_1 = 2
        val KM_10 = 3
        val AUTO = 4
        fun default() = Radius(AUTO)
    }

    fun getMaxRadiusInMeters(requirements: Requirements)
            = when (type) {
        METER_200 -> 200
        METER_500 -> 500
        KM_1 -> 1000
        KM_10 -> 10000
        AUTO -> getAutoRadius(requirements)
        else -> throw UnsupportedOperationException("Unknown type: $type")
    }

    private fun getAutoRadius(requirements: Requirements): Int =
            (requirements.settings.transportMode.getSpeedPerMinute()
                    * requirements.givenTravelTime()).toInt()

}

package com.mimi.destinationfinder.utils

import com.mimi.destinationfinder.dto.GooglePlace
import com.mimi.destinationfinder.dto.Location

import com.mimi.destinationfinder.dto.TransportMean
import java.util.concurrent.TimeUnit

/**
 * Created by Mimi on 17/11/2017.
 * This class will filter the received places by the distance to the initial place
 */
class FilterPlacesUtil {
    private val timeCalculator = TimeCalculator()

    fun filterPlaces(initialList: List<GooglePlace>,
                     initialLocation: Location, timeToGetThere: Long,
                     transportMean: TransportMean, maxResults: Int): List<GooglePlace> {
        val idealTime = TimeUnit.MILLISECONDS.toMinutes(timeToGetThere)
        initialList.forEach {
            val calculatedTime = timeCalculator.calculateTime(
                    from = initialLocation, to = it.geometry.location,
                    mean = transportMean)
            it.errorMargin = Math.abs(calculatedTime - idealTime)
        }
        val listReorderedByMinutes = initialList.sortedBy {
            it.errorMargin

        }
        return if (listReorderedByMinutes.size < maxResults)
            listReorderedByMinutes
        else
            listReorderedByMinutes.subList(0, maxResults)
    }
}

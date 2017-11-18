package com.mimi.destinationfinder.repository.googleApi

import com.mimi.destinationfinder.dto.GooglePlace
import com.mimi.destinationfinder.dto.Requirements

import com.mimi.destinationfinder.utils.TimeCalculator

/**
 * Created by Mimi on 17/11/2017.
 * This class will filter the received places by the distance to the initial place
 */
class FilterPlacesUtil {
    private val timeCalculator = TimeCalculator()

    fun filterPlaces(initialList: List<GooglePlace>, data:Requirements): List<GooglePlace> {

        initialList.forEach {
            val calculatedTime = timeCalculator.calculateTime(
                    from = data.initialLocation, to = it.geometry.location,
                    mean = data.transportMean)

            it.errorMargin = Math.abs(calculatedTime - data.getTravelTimeInMinutes())
        }

        val listReorderedByMinutes = initialList.sortedBy {
            it.errorMargin

        }
        return if (listReorderedByMinutes.size < data.maxResults)
            listReorderedByMinutes
        else
            listReorderedByMinutes.subList(0, data.maxResults.toInt())
    }
}

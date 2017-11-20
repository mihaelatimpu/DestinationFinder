package com.mimi.destinationfinder.repository.retrofit

import com.mimi.destinationfinder.dto.GooglePlace
import com.mimi.destinationfinder.dto.Requirements

import com.mimi.destinationfinder.utils.TimeCalculator

/**
 * Created by Mimi on 17/11/2017.
 * This class will filter the received places by the distance to the initial place
 */
class FilterPlacesUtil {
    private val timeCalculator = TimeCalculator()

    fun calculateTravelTime(initialList: List<GooglePlace>, data: Requirements): List<GooglePlace> {
        initialList.forEach {
            val calculatedTime = timeCalculator.calculateTime(
                    from = data.initialCoordinates, to = it.geometry.location,
                    mean = data.settings.transportMode)

            it.errorMargin = Math.abs(calculatedTime - data.givenTravelTime())
        }
        return initialList
    }

    fun getBestPlaces(initialList: List<GooglePlace>, data: Requirements): List<GooglePlace> {
        val rankedLst = initialList.sortedBy { it.errorMargin }
        return if (rankedLst.size < data.settings.maxResults.getMaxResults())
            rankedLst
        else
            rankedLst.subList(0, data.settings.maxResults.getMaxResults())
    }
}

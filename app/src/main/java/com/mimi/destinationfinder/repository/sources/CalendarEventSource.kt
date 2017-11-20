package com.mimi.destinationfinder.repository.sources

import android.content.Context
import com.mimi.destinationfinder.dto.Coordinates
import com.mimi.destinationfinder.dto.Event
import com.mimi.destinationfinder.dto.Location
import com.mimi.destinationfinder.dto.Requirements
import com.mimi.destinationfinder.repository.events.CalendarProvider
import com.mimi.destinationfinder.utils.GeocoderUtil
import io.reactivex.Observable

/**
 * Created by Mimi on 18/11/2017.
 * The addresses are being taken from the calendar events
 */
class CalendarEventSource(override val data: Requirements) : BaseSource {
    companion object {
        private val ONE_MINUTE = 60000
    }

    private val converter = GeocoderUtil()
    override fun start(context: Context): Observable<Location>
            = Observable.create({ e ->
        val minTime = data.arrivalTime.timeInMillis - 5 * ONE_MINUTE
        val maxTime = data.arrivalTime.timeInMillis + 5 * ONE_MINUTE
        val events = CalendarProvider().getEvents(context.contentResolver, minTime, maxTime)
                .filter { it.location.isNotEmpty() }
        val destinations = events.map { toLocation(context, it) }

        destinations.forEach { e.onNext(it) }

        e.onComplete()
    })

    private fun toLocation(context: Context, event: Event): Location {
        val defaultDestination = Location(cityName = event.location, errorMargin = 1.0)
        val coordinates = toCoordinates(event.location)
                ?: return defaultDestination
        val destination = converter.getAddress(context, coordinates) ?: defaultDestination
        destination.arrivalTime = event.eventStartTime
        return destination
    }

    private fun toCoordinates(string: String): Coordinates? {
        return try {
            val split = string.split(",")
            Coordinates(split.first().toDouble(), split[1].toDouble())
        } catch (e: Exception) {
            null
        }
    }

}

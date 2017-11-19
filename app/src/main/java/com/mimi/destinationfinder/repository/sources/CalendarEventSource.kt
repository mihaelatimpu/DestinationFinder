package com.mimi.destinationfinder.repository.sources

import android.content.Context
import com.mimi.destinationfinder.dto.Destination
import com.mimi.destinationfinder.dto.Event
import com.mimi.destinationfinder.dto.Location
import com.mimi.destinationfinder.dto.Requirements
import com.mimi.destinationfinder.repository.events.CalendarProvider
import com.mimi.destinationfinder.utils.GeocoderUtil
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe

/**
 * Created by Mimi on 18/11/2017.
 * The addresses are being taken from the calendar events
 */
class CalendarEventSource(override val data: Requirements) : BaseSource {
    companion object {
        private val ONE_MINUTE = 60000
    }

    private val converter = GeocoderUtil()
    override fun start(context: Context)
            = Observable.create(ObservableOnSubscribe<Destination> { e ->
        val minTime = data.arrivalTime.timeInMillis - 5 * ONE_MINUTE
        val maxTime = data.arrivalTime.timeInMillis + 5 * ONE_MINUTE
        val events = CalendarProvider().getEvents(context.contentResolver, minTime, maxTime)
                .filter { it.location.isNotEmpty() }
        val destinations = events.map {
            convert(context, it)
        }
        destinations.forEach {
            e.onNext(it)
        }
        e.onComplete()
    })!!

    private fun convert(context: Context, event: Event): Destination {
        val defaultDestination = Destination(cityName = event.location, errorMargin = 1.0)
        val location = convertStringToLocation(event.location)
                ?: return defaultDestination
        val destination =  converter.getAddress(context, location) ?: defaultDestination
        destination.arrivalTime = event.eventStartTime
        return destination
    }

    private fun convertStringToLocation(string: String): Location? {
        return try {
            val split = string.split(",")
            Location(split.first().toDouble(), split[1].toDouble())
        } catch (e: Exception) {
            null
        }
    }

}

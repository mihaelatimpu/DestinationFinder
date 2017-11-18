package com.mimi.destinationfinder.repository

import android.content.ContentResolver
import android.content.Context
import com.mimi.destinationfinder.dto.Address
import com.mimi.destinationfinder.dto.GooglePlace
import com.mimi.destinationfinder.dto.Location
import com.mimi.destinationfinder.dto.TransportMean
import com.mimi.destinationfinder.repository.events.CalendarProvider
import com.mimi.destinationfinder.repository.retrofit.requests.RequestAllAddresses
import com.mimi.destinationfinder.repository.retrofit.requests.RequestGetPlaces
import com.mimi.destinationfinder.utils.FilterPlacesUtil
import java.util.*

/**
 * Created by Mimi on 17/11/2017.
 *
 */
class PlacesRepository {
    fun getEventsFromCalendar(beginTime: Calendar, endTime: Calendar, cr: ContentResolver) =
            CalendarProvider().getEvents(beginTime, endTime, cr)

    fun findPlacesWithGoogleApi(context: Context, location: Location, radius: Int,
                                types: List<String>,
                                onCompleted: (List<Address>) -> Unit,
                                transportMean:TransportMean,
                                maxResults:Int, timeToGetThere:Long) {
        RequestGetPlaces(location, radius, types).start(context){
            val filteredResults = FilterPlacesUtil().filterPlaces(
                    initialList = it,initialLocation = location,timeToGetThere = timeToGetThere,
                    maxResults = maxResults, transportMean = transportMean)
            findAddresses(context = context, places = filteredResults,onCompleted = onCompleted)
        }
    }
    private fun findAddresses(context: Context,places:List<GooglePlace>,onCompleted: (List<Address>) -> Unit){
        RequestAllAddresses(places)
                .start(context){
                    val reorderedAddresses = it.sortedBy { it.errorMargin }
                    onCompleted(reorderedAddresses)
                }
    }
}
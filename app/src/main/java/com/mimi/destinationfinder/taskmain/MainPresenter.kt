package com.mimi.destinationfinder.taskmain

import android.util.Log
import com.google.android.gms.location.places.Place
import com.mimi.destinationfinder.R
import com.mimi.destinationfinder.dto.*
import com.mimi.destinationfinder.repository.PlacesRepository
import com.mimi.destinationfinder.utils.TimeConverter
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete
import java.util.*

/**
 * Created by Mimi on 17/11/2017.
 *
 */
class MainPresenter : MainContract.Presenter {

    override lateinit var view: MainContract.View
    private val timeConverter = TimeConverter()
    private val departureTime = Calendar.getInstance()
    private val arrivalTime = Calendar.getInstance()
    private var departurePlace: Place? = null
    private val placeTypes = listOf("airport", "hospital", "amusement_park", "aquarium", "bank", "bar", "gym"
            , "meal_takeaway", "cafe", "dentist", "university")
    private val placesRepository = PlacesRepository()
    private val transportMean = TransportMean(TransportMean.TYPE_WALKING)
    private val possibleDestinations = arrayListOf<Address>()

    override fun start() {
        if (!view.isActive)
            return
        view.init()
        view.setDepartureTime(timeConverter.convertToRegularTimeString(departureTime))
        view.setDepartureDate(timeConverter.convertToRegularDateString(departureTime))

        view.setArrivalTime(timeConverter.convertToRegularTimeString(arrivalTime))
        view.setArrivalDate(timeConverter.convertToRegularDateString(arrivalTime))
        view.setDeparturePlace(getDeparturePlace())

    }

    private fun getDeparturePlace() = departurePlace?.name.toString()

    override fun onDeparturePlaceClicked() {
        if (!view.isActive)
            return
        view.startPlaceAutocompleteActivity()
    }

    override fun onDepartureDateClicked() {
        if (!view.isActive)
            return
        view.showDatePicker(initialDate = departureTime) {
            if (departureTime.timeInMillis > it.timeInMillis) {
                view.toast(R.string.error_invalid_arrival)
                return@showDatePicker
            }
            departureTime.timeInMillis = it.timeInMillis
            view.setDepartureDate(timeConverter.convertToRegularDateString(departureTime))
        }
    }

    override fun onArrivalDateClicked() {
        if (!view.isActive)
            return
        view.showDatePicker(initialDate = arrivalTime, minDate = departureTime) {
            arrivalTime.timeInMillis = it.timeInMillis
            view.setArrivalDate(timeConverter.convertToRegularDateString(arrivalTime))
        }
    }

    override fun onStartButtonPressed() {
        if (!view.isActive) return
        if (departurePlace == null) {
            view.toast(R.string.please_select_departure_location)
            return
        }
        view.checkForPermission(permission = android.Manifest.permission.READ_CALENDAR,
                title = R.string.requesting_read_events_permission,
                description = R.string.requesting_read_event_permission_description) {
            //loadAllCalendarEvents()
            findPlacesInRange()
        }
    }

    private fun findPlacesInRange() {
        view.showLoadingDialog()
        possibleDestinations.clear()
        doAsync {
            val timeDifference = arrivalTime.timeInMillis - departureTime.timeInMillis
            val location = Location(departurePlace!!.latLng)
            placesRepository.findPlacesWithGoogleApi(context = view.getContext(),
                    location = location,
                    radius = 500,
                    transportMean = transportMean, maxResults = 50, timeToGetThere = timeDifference,
                    types = placeTypes, onCompleted = {
                possibleDestinations.addAll(it)
                if (possibleDestinations.isEmpty())
                    view.hideArrivalAddress()
                else
                    view.showArrivalAddress(possibleDestinations.first().toString())
                view.hideLoadingDialog()
            })
        }
    }

    override fun onChangeArrivalPressed() {
        if (!view.isActive) return
        val list = possibleDestinations.map { it.toString() }
        view.showListSelector(list) {
            view.showArrivalAddress(it.toString())
        }
    }

    private fun loadAllCalendarEvents() {
        view.showLoadingDialog()
        val events = arrayListOf<Event>()
        doAsync {
            try {
                events.addAll(placesRepository.getEventsFromCalendar(beginTime = departureTime,
                        endTime = arrivalTime, cr = view.getContentResolver()))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            onComplete {
                Log.d("Events received", "Events; ${events.size}")
                view.hideLoadingDialog()
            }
        }

    }

    override fun onDepartureTimeClicked() {
        if (!view.isActive)
            return
        view.showTimePicker(initialDate = departureTime) {
            departureTime.timeInMillis = it.timeInMillis
            view.setDepartureTime(timeConverter.convertToRegularTimeString(departureTime))
        }
    }

    override fun onArrivalTimeClicked() {
        if (!view.isActive)
            return
        view.showTimePicker(initialDate = arrivalTime) {
            if (departureTime.timeInMillis > it.timeInMillis) {
                view.toast(R.string.error_invalid_arrival)
                return@showTimePicker
            }
            arrivalTime.timeInMillis = it.timeInMillis
            view.setArrivalTime(timeConverter.convertToRegularTimeString(arrivalTime))
        }
    }

    override fun onDeparturePlaceSelected(place: Place) {
        departurePlace = place
        view.setDeparturePlace(place.name.toString())
    }

}
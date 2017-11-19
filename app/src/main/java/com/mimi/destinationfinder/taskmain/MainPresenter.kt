package com.mimi.destinationfinder.taskmain

import com.google.android.gms.location.places.Place
import com.mimi.destinationfinder.R
import com.mimi.destinationfinder.dto.Destination
import com.mimi.destinationfinder.dto.Location
import com.mimi.destinationfinder.dto.Requirements
import com.mimi.destinationfinder.repository.SharedPreferenceUtils
import com.mimi.destinationfinder.repository.sources.BaseSource
import com.mimi.destinationfinder.repository.sources.CalendarEventSource
import com.mimi.destinationfinder.repository.sources.GoogleApiSource
import com.mimi.destinationfinder.repository.sources.MainDestinationSource
import com.mimi.destinationfinder.utils.GeocoderUtil
import com.mimi.destinationfinder.utils.TimeConverter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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
    private val requirements by lazy {
        val rq = Requirements()
        rq.arrivalTime.add(Calendar.HOUR, 1)
        rq
    }

    private var departurePlace: Place? = null
    private var departureLocation: Destination? = null

    private val possibleDestinations = arrayListOf<Destination>()

    override fun start() {
        if (!view.isActive)
            return
        view.init()
        view.setDepartureTime(timeConverter.convertToRegularTimeString(requirements.departureTime))
        view.setDepartureDate(timeConverter.convertToRegularDateString(requirements.departureTime))

        view.setArrivalTime(timeConverter.convertToRegularTimeString(requirements.arrivalTime))
        view.setArrivalDate(timeConverter.convertToRegularDateString(requirements.arrivalTime))
        view.setDeparturePlace(getDeparturePlace())
        getCurrentLocationFromGPS()
        reloadSettings()
    }

    override fun reloadSettings() {
        view.showLoadingDialog()
        doAsync {
            requirements.settings = SharedPreferenceUtils.retrieveSettings(view.getContext())
            onComplete {
                view.hideLoadingDialog()
            }
        }
    }

    private fun getCurrentLocationFromGPS() {
        view.getMainActivity().checkForPermission(permission = android.Manifest.permission.ACCESS_FINE_LOCATION,
                title = R.string.missing_permission,
                description = R.string.access_gsp_location_permission_explained) {
            if (it) {
                departureLocation = view.getCurrentLocation(fromGPS = true)
                if (departureLocation != null) {
                    view.setDeparturePlace(departureLocation.toString())
                } else {
                    getCurrentLocationFromInternet()
                }
            } else {
                getCurrentLocationFromInternet()
            }
        }
    }

    private fun getCurrentLocationFromInternet() {
        view.getMainActivity().checkForPermission(permission = android.Manifest.permission.ACCESS_COARSE_LOCATION,
                title = R.string.missing_permission,
                description = R.string.access_network_location_permission_explained) {
            if (it) {
                departureLocation = view.getCurrentLocation(fromNetwork = true)
                if (departureLocation != null) {
                    view.setDeparturePlace(departureLocation.toString())
                }
            }
        }
    }

    private fun getDeparturePlace() = departurePlace?.name.toString()

    override fun onDeparturePlaceClicked() {
        if (!view.isActive)
            return
        view.getMainActivity().startPlaceAutocompleteActivity()
    }

    override fun onDepartureDateClicked() {
        if (!view.isActive)
            return
        view.getMainActivity().showDatePicker(initialDate = requirements.departureTime) {
            if (requirements.departureTime.timeInMillis > it.timeInMillis) {
                view.toast(R.string.error_invalid_arrival)
                return@showDatePicker
            }
            requirements.departureTime.timeInMillis = it.timeInMillis
            view.setDepartureDate(timeConverter.convertToRegularDateString(
                    requirements.departureTime))
        }
    }

    override fun onArrivalDateClicked() {
        if (!view.isActive)
            return
        view.getMainActivity().showDatePicker(initialDate = requirements.arrivalTime,
                minDate = requirements.departureTime) {
            requirements.arrivalTime.timeInMillis = it.timeInMillis
            view.setArrivalDate(
                    timeConverter.convertToRegularDateString(requirements.arrivalTime))
        }
    }

    override fun onStartButtonPressed() {
        if (!view.isActive) return
        if (departurePlace == null && departureLocation == null) {
            view.toast(R.string.please_select_departure_location)
            return
        }
        view.getMainActivity().checkForPermission(permission = android.Manifest.permission.READ_CALENDAR,
                title = R.string.missing_permission,
                description = R.string.requesting_read_event_permission_description) {
            //loadAllCalendarEvents()
            findPlacesInRange(it, true)
        }
    }

    private fun findPlacesInRange(searchCalendar: Boolean, searchApi: Boolean) {
        view.showLoadingDialog()
        possibleDestinations.clear()
        if (departurePlace != null)
            requirements.initialLocation = Location(departurePlace!!.latLng)
        else
            requirements.initialLocation = departureLocation!!.location!!

        val sources = arrayListOf<BaseSource>()
        if (searchCalendar)
            sources.add(CalendarEventSource(requirements))
        if (searchApi)
            sources.add(GoogleApiSource(requirements))

        MainDestinationSource(requirements, sources).start(view.getContext())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError {
                    view.toast(it.message.toString())
                    it.printStackTrace()
                    view.hideLoadingDialog()
                }
                .doOnNext {
                    possibleDestinations.add(it)
                    if (possibleDestinations.size == 1) {
                        view.showArrivalAddress(it.toString())
                    }
                }
                .doOnComplete {
                    val sortedList = possibleDestinations.sortedBy { it.errorMargin }
                    possibleDestinations.clear()
                    possibleDestinations.addAll(sortedList)
                    if (possibleDestinations.isNotEmpty())
                        view.showArrivalAddress(possibleDestinations.first().toString())
                    view.hideLoadingDialog()
                }
                .subscribe()
    }

    override fun onChangeArrivalPressed() {
        if (!view.isActive) return
        val list = possibleDestinations.map { it.toStringWithArrivalTime() }
        view.showListSelector(list) {
            view.showArrivalAddress(it)
        }
    }


    override fun onDepartureTimeClicked() {
        if (!view.isActive)
            return
        view.getMainActivity().showTimePicker(initialDate = requirements.departureTime) {
            requirements.departureTime.timeInMillis = it.timeInMillis
            view.setDepartureTime(timeConverter.convertToRegularTimeString(requirements.departureTime))
        }
    }

    override fun onArrivalTimeClicked() {
        if (!view.isActive)
            return
        view.getMainActivity().showTimePicker(initialDate = requirements.arrivalTime) {
            if (requirements.departureTime.timeInMillis > it.timeInMillis) {
                view.toast(R.string.error_invalid_arrival)
                return@showTimePicker
            }
            requirements.arrivalTime.timeInMillis = it.timeInMillis
            if (view.isActive)
                view.setArrivalTime(timeConverter.convertToRegularTimeString(requirements.arrivalTime))
        }
    }

    override fun onDeparturePlaceSelected(place: Place) {
        departurePlace = place
        departureLocation = GeocoderUtil().getAddress(view.getContext(),
                Location(place.latLng.latitude, place.latLng.longitude))
        if (view.isActive) {
            if (departureLocation != null)
                view.setDeparturePlace(departureLocation.toString())
            else
                view.setDeparturePlace(place.name.toString())
        }
    }

}
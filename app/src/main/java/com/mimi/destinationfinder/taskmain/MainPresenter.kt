package com.mimi.destinationfinder.taskmain

import com.google.android.gms.location.places.Place
import com.mimi.destinationfinder.R
import com.mimi.destinationfinder.dto.Location
import com.mimi.destinationfinder.dto.Coordinates
import com.mimi.destinationfinder.dto.Requirements
import com.mimi.destinationfinder.repository.SharedPreferenceUtils
import com.mimi.destinationfinder.repository.sources.BaseSource
import com.mimi.destinationfinder.repository.sources.CalendarEventSource
import com.mimi.destinationfinder.repository.sources.GoogleApiSource
import com.mimi.destinationfinder.repository.sources.MainDestinationSource
import com.mimi.destinationfinder.utils.GeocoderUtil
import com.mimi.destinationfinder.utils.TimeConverter
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete

/**
 * Created by Mimi on 17/11/2017.
 *
 */
class MainPresenter : MainContract.Presenter {

    override lateinit var view: MainContract.View

    private val converter = TimeConverter()
    private val req by lazy { Requirements.initialRequirements() }

    private var departurePlace: Place? = null
    private var departureLocation: Location? = null

    private val possibleDestinations = arrayListOf<Location>()

    override fun start() {
        if (!view.isActive)
            return
        view.init()
        view.setDepartureTime(converter.toRegularTimeString(req.departureTime))
        view.setDepartureDate(converter.toRegularDateString(req.departureTime))

        view.setArrivalTime(converter.toRegularTimeString(req.arrivalTime))
        view.setArrivalDate(converter.toRegularDateString(req.arrivalTime))

        view.setDeparturePlace(departurePlace?.name.toString())
        getCurrentLocationFromGPS()
        reloadSettings()
    }

    override fun reloadSettings() {
        view.showLoadingDialog()
        doAsync {
            req.settings = SharedPreferenceUtils.retrieveSettings(view.getContext())
            onComplete {
                view.hideLoadingDialog()
            }
        }
    }

    private fun getCurrentLocationFromGPS() {
        getCurrentLocation(fromInternet = false) {
            if (departureLocation != null) {
                view.setDeparturePlace(departureLocation.toString())
            } else {
                getCurrentLocationFromInternet()
            }

        }
    }

    private fun getCurrentLocationFromInternet() {
        getCurrentLocation(fromInternet = true) {
            if (departureLocation != null) {
                view.setDeparturePlace(departureLocation.toString())
            }
        }
    }

    private fun getCurrentLocation(fromInternet: Boolean, onCompleted: () -> Unit) {
        val requiredPermission = if (fromInternet)
            android.Manifest.permission.ACCESS_FINE_LOCATION
        else
            android.Manifest.permission.ACCESS_COARSE_LOCATION

        val description = if (fromInternet) R.string.gps_explanation
        else R.string.network_explanation

        view.getMainActivity().checkForPermission(
                permission = requiredPermission,
                description = description,
                title = R.string.missing_permission) {
            if (it) {
                departureLocation = view.getCurrentLocation(fromNetwork = fromInternet,
                        fromGPS = !fromInternet)
                onCompleted()
            } else {
                onCompleted()
            }
        }
    }


    override fun onDeparturePlaceClicked() {
        if (!view.isActive) return
        if (!view.getMainActivity().isInternetConnected()) {
            view.toast(R.string.no_internet_error)
            return
        }
        view.getMainActivity().startPlaceAutocompleteActivity()
    }

    override fun onDepartureDateClicked() {
        if (!view.isActive) return
        view.getMainActivity().showDatePicker(initialDate = req.departureTime) {
            if (req.departureTime.timeInMillis > it.timeInMillis) {
                view.toast(R.string.error_invalid_arrival)
                return@showDatePicker
            }
            req.departureTime.timeInMillis = it.timeInMillis
            view.setDepartureDate(converter.toRegularDateString(
                    req.departureTime))
        }
    }

    override fun onArrivalDateClicked() {
        if (!view.isActive) return
        view.getMainActivity().showDatePicker(initialDate = req.arrivalTime,
                minDate = req.departureTime) {
            req.arrivalTime.timeInMillis = it.timeInMillis
            view.setArrivalDate(
                    converter.toRegularDateString(req.arrivalTime))
        }
    }

    override fun startSearch() {
        if (!view.isActive) return
        if (!view.getMainActivity().isInternetConnected()) {
            view.toast(R.string.no_internet_error)
            return
        }
        if (departurePlace == null && departureLocation == null) {
            view.toast(R.string.please_select_departure_location)
            return
        }
        view.getMainActivity().checkForPermission(permission = android.Manifest.permission.READ_CALENDAR,
                title = R.string.missing_permission,
                description = R.string.requesting_read_event_permission_description) {
            findPlacesInRange(it, true)
        }
    }

    private fun findPlacesInRange(searchCalendar: Boolean, searchApi: Boolean) {
        view.showLoadingDialog()
        possibleDestinations.clear()
        if (departurePlace != null)
            req.initialCoordinates = Coordinates(departurePlace!!.latLng)
        else
            req.initialCoordinates = departureLocation!!.coordinates!!

        val sources = arrayListOf<BaseSource>()
        if (searchCalendar) sources.add(CalendarEventSource(req))
        if (searchApi) sources.add(GoogleApiSource(req))

        MainDestinationSource(req, sources).start(view.getContext())
                .doOnError { manageError(it) }
                .doOnNext { onNewDestinationReceived(it) }
                .doOnComplete { onFinishedLoadingDestinations() }
                .subscribe()
    }

    private fun manageError(error: Throwable) {
        view.toast(error.message.toString())
        error.printStackTrace()
        view.hideLoadingDialog()

    }

    private fun onNewDestinationReceived(destination: Location) {
        possibleDestinations.add(destination)
        if (possibleDestinations.size == 1) {
            view.showDestination(destination.toString())
        }
    }

    private fun onFinishedLoadingDestinations() {
        val sortedList = possibleDestinations.sortedBy { it.errorMargin }
        possibleDestinations.clear()
        possibleDestinations.addAll(sortedList)
        if (possibleDestinations.isEmpty()) {
            view.toast(R.string.no_destination_found)
            view.hideDestination()
        } else {
            view.showDestination(possibleDestinations.first().toString())
        }
        view.hideLoadingDialog()

    }

    override fun onChangeArrivalPressed() {
        if (!view.isActive) return
        val list = possibleDestinations.map { it.toStringWithArrivalTime() }
        view.showListSelector(list) {
            view.showDestination(possibleDestinations[it].toString())
        }
    }


    override fun onDepartureTimeClicked() {
        if (!view.isActive)
            return
        view.getMainActivity().showTimePicker(initialDate = req.departureTime) {
            req.departureTime.timeInMillis = it.timeInMillis
            view.setDepartureTime(converter.toRegularTimeString(req.departureTime))
        }
    }

    override fun onArrivalTimeClicked() {
        if (!view.isActive)
            return
        view.getMainActivity().showTimePicker(initialDate = req.arrivalTime) {
            if (req.departureTime.timeInMillis > it.timeInMillis) {
                view.toast(R.string.error_invalid_arrival)
                return@showTimePicker
            }
            req.arrivalTime.timeInMillis = it.timeInMillis
            if (view.isActive)
                view.setArrivalTime(converter.toRegularTimeString(req.arrivalTime))
        }
    }

    override fun onDeparturePlaceSelected(place: Place) {
        departurePlace = place
        departureLocation = GeocoderUtil().getAddress(view.getContext(),
                Coordinates(place.latLng.latitude, place.latLng.longitude))
        if (view.isActive) {
            if (departureLocation != null)
                view.setDeparturePlace(departureLocation.toString())
            else
                view.setDeparturePlace(place.name.toString())
        }
    }

}
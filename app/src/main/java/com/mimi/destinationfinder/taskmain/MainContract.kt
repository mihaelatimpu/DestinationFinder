package com.mimi.destinationfinder.taskmain

import android.support.annotation.StringRes
import com.google.android.gms.location.places.Place
import com.mimi.destinationfinder.base.BasePresenter
import com.mimi.destinationfinder.base.BaseView
import com.mimi.destinationfinder.dto.Location
import java.util.*

/**
 * Created by Mimi on 17/11/2017.
 *
 */
interface MainContract{
    interface View:BaseView<Presenter>{
        fun setDeparturePlace(place:String)
        fun setDepartureDate(date:String)
        fun setDepartureTime(time:String)
        fun setArrivalDate(date:String)
        fun setArrivalTime(time:String)
        fun showDestination(address:String)
        fun hideDestination()
        fun showListSelector(options:List<String>, onSelected:(Int)->Unit)
        fun getCurrentLocation(fromGPS:Boolean = false, fromNetwork:Boolean = false): Location?
        fun getMainActivity():Activity
    }
    interface Activity{
        fun showDatePicker(initialDate: Calendar,minDate:Calendar? = null, onComplete:(Calendar)->Unit)
        fun showTimePicker(initialDate: Calendar, onComplete:(Calendar)->Unit)
        fun startPlaceAutocompleteActivity()
        fun checkForPermission(permission: String, @StringRes title:Int,
                               @StringRes description:Int, onPermissionResult:(Boolean)->Unit)
        fun isInternetConnected():Boolean

    }
    interface Presenter:BasePresenter<View>{
        fun onDeparturePlaceClicked()
        fun onDepartureDateClicked()
        fun onArrivalDateClicked()
        fun onDepartureTimeClicked()
        fun onArrivalTimeClicked()
        fun onDeparturePlaceSelected(place:Place)
        fun startSearch()
        fun onChangeArrivalPressed()
        fun reloadSettings()
    }
}
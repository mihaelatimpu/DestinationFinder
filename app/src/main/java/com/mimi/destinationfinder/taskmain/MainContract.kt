package com.mimi.destinationfinder.taskmain

import android.content.ContentResolver
import android.support.annotation.StringRes
import com.google.android.gms.location.places.Place
import com.mimi.destinationfinder.base.BasePresenter
import com.mimi.destinationfinder.base.BaseView
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
        fun showDatePicker(initialDate: Calendar,minDate:Calendar? = null, onComplete:(Calendar)->Unit)
        fun showTimePicker(initialDate: Calendar, onComplete:(Calendar)->Unit)
        fun startPlaceAutocompleteActivity()
        fun getContentResolver():ContentResolver
        fun showArrivalAddress(address:String)
        fun hideArrivalAddress()
        fun showListSelector(options:List<String>, onSelected:(String)->Unit)
        fun checkForPermission(permission: String,@StringRes title:Int, @StringRes description:Int, onPermissionGranted:()->Unit)
    }
    interface Presenter:BasePresenter<View>{
        fun onDeparturePlaceClicked()
        fun onDepartureDateClicked()
        fun onArrivalDateClicked()
        fun onDepartureTimeClicked()
        fun onArrivalTimeClicked()
        fun onDeparturePlaceSelected(place:Place)
        fun onStartButtonPressed()
        fun onChangeArrivalPressed()
    }
}
package com.mimi.destinationfinder.taskmain

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.location.places.ui.PlacePicker
import com.mimi.destinationfinder.R
import com.mimi.destinationfinder.base.BaseFragment
import com.mimi.destinationfinder.dto.Destination
import com.mimi.destinationfinder.utils.Context
import com.mimi.destinationfinder.utils.CurrentLocationUtil
import kotlinx.android.synthetic.main.fragment_main.*
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.selector
import org.koin.android.ext.android.inject
import java.util.*


/**
 * Created by Mimi on 17/11/2017.
 *
 */
class MainFragment : BaseFragment(), MainContract.View {
    companion object {
        val PLACE_PICKER_REQUEST = 34545
        val REQUESTING_PERMISSION = 4545
    }

    override var isActive: Boolean = false
        get() = isAdded

    override val contextName = Context.DestinationFinder
    override val presenter: MainContract.Presenter by inject()
    private var onPermissionResult: (Boolean) -> Unit = {}


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_main, container, false)

    override fun onAttachFragment(childFragment: Fragment?) {
        super.onAttachFragment(childFragment)
    }
    override fun onStart() {
        super.onStart()
        presenter.view = this
        presenter.start()
    }


    override fun init() {
        departureDate.setOnClickListener { presenter.onDepartureDateClicked() }
        departureDateLabel.setOnClickListener { presenter.onDepartureDateClicked() }

        departureLocation.setOnClickListener { presenter.onDeparturePlaceClicked() }
        departureLocationLabel.setOnClickListener { presenter.onDeparturePlaceClicked() }

        departureTime.setOnClickListener { presenter.onDepartureTimeClicked() }
        departureTimeLabel.setOnClickListener { presenter.onDepartureTimeClicked() }

        arrivalDate.setOnClickListener { presenter.onArrivalDateClicked() }
        arrivalDateLabel.setOnClickListener { presenter.onArrivalDateClicked() }

        arrivalTime.setOnClickListener { presenter.onArrivalTimeClicked() }
        arrivalTimeLabel.setOnClickListener { presenter.onArrivalTimeClicked() }

        startButton.setOnClickListener { presenter.onStartButtonPressed() }

        changeArrivalLocation.setOnClickListener { presenter.onChangeArrivalPressed() }
        changeArrivalLocation.visibility = View.INVISIBLE
    }

    override fun setArrivalDate(date: String) {
        arrivalDate.text = date
    }

    override fun setArrivalTime(time: String) {
        arrivalTime.text = time
    }

    override fun setDepartureDate(date: String) {
        departureDate.text = date
    }

    override fun setDeparturePlace(place: String) {
        try {
            departureLocation.text = place
        } catch (e: Exception) {
            toast(e.message.toString())
            e.printStackTrace()
        }
    }

    override fun getCurrentLocation(fromGPS: Boolean, fromNetwork: Boolean): Destination? {
        val provider = when {
            fromGPS -> LocationManager.GPS_PROVIDER
            fromNetwork -> LocationManager.NETWORK_PROVIDER
            else -> return null
        }
        return CurrentLocationUtil().findLocation(context, provider)
    }

    override fun checkForPermission(permission: String, title: Int,
                                    description: Int, onPermissionResult: (Boolean) -> Unit) {
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            onPermissionResult(true)
            return
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            alert(message = description, title = title) {
                positiveButton(R.string.grant_permission) {
                    ActivityCompat.requestPermissions(activity, arrayOf(permission), REQUESTING_PERMISSION)
                }
                onCancelled {
                    onPermissionResult(false)
                }
            }.show()
        } else {
            this.onPermissionResult = onPermissionResult
            ActivityCompat.requestPermissions(activity, arrayOf(permission), REQUESTING_PERMISSION)
        }

    }


    override fun setDepartureTime(time: String) {
        departureTime.text = time
    }

    override fun showDatePicker(initialDate: Calendar, minDate: Calendar?, onComplete: (Calendar) -> Unit) {
        val initialYear = initialDate.get(Calendar.YEAR)
        val initialMonth = initialDate.get(Calendar.MONTH)
        val initialDay = initialDate.get(Calendar.DAY_OF_MONTH)
        val datePicker = DatePickerDialog(context,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    initialDate.set(Calendar.YEAR, year)
                    initialDate.set(Calendar.MONTH, month)
                    initialDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    onComplete(initialDate)
                }, initialYear, initialMonth, initialDay)
        if (minDate != null)
            datePicker.datePicker.minDate = minDate.timeInMillis
        datePicker.show()
    }

    override fun showTimePicker(initialDate: Calendar,
                                onComplete: (Calendar) -> Unit) {
        val initialHour = initialDate.get(Calendar.HOUR_OF_DAY)
        val initialMinute = initialDate.get(Calendar.MINUTE)
        val timePicker = TimePickerDialog(context,
                TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    initialDate.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    initialDate.set(Calendar.MINUTE, minute)
                    onComplete(initialDate)
                }, initialHour, initialMinute, true)
        timePicker.show()
    }

    override fun showArrivalAddress(address: String) {
        arrivalPlace.post {
            arrivalPlace.text = address
            changeArrivalLocation.visibility = View.VISIBLE
            arrivalPlace.visibility = View.VISIBLE
        }
    }

    override fun hideArrivalAddress() {
        changeArrivalLocation.visibility = View.INVISIBLE
        arrivalPlace.visibility = View.INVISIBLE
    }

    override fun showListSelector(options: List<String>, onSelected: (String) -> Unit) {
        selector(title = getText(R.string.select_arrival_location), items = options,
                onClick = { _, position -> onSelected(options[position]) })
    }

    override fun startPlaceAutocompleteActivity() {
        try {
            val intent = PlacePicker.IntentBuilder()
                    .build(context as Activity)
            (context as Activity).startActivityForResult(intent, PLACE_PICKER_REQUEST)
        } catch (e: Exception) {
            e.printStackTrace()
            toast(e.message + "")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PLACE_PICKER_REQUEST -> {
                    val place = PlacePicker.getPlace(context, data)
                    presenter.onDeparturePlaceSelected(place)
                }
                REQUESTING_PERMISSION -> onPermissionResult(true)
                else -> {
                }
            }
        }
    }

    override fun getContentResolver() = context.contentResolver as ContentResolver
}

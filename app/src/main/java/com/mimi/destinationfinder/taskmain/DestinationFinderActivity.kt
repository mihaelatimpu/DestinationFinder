package com.mimi.destinationfinder.taskmain

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.location.places.ui.PlacePicker
import com.mimi.destinationfinder.R
import com.mimi.destinationfinder.settings.SettingsActivity
import com.mimi.destinationfinder.utils.replaceFragmentInActivity
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import org.koin.android.ext.android.inject
import java.util.*

/**
 *
 */
class DestinationFinderActivity : AppCompatActivity(), MainContract.Activity {
    companion object {
        val SETTINGS_ACTIVITY = 34564
        val PLACE_PICKER_REQUEST = 34545
        val REQUESTING_PERMISSION = 4545
    }

    private var mVisible: Boolean = false
    private val fragment: MainFragment by inject()
    private val presenter: MainContract.Presenter by inject()

    private var onPermissionResult: (Boolean) -> Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_destination_finder)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        mVisible = true
        supportFragmentManager.findFragmentById(R.id.contentFrame) as MainFragment?
                ?: fragment.also {
            replaceFragmentInActivity(it, R.id.contentFrame)
        }
        presenter.view = fragment
    }

    override fun isInternetConnected()
            = (getSystemService(Context.CONNECTIVITY_SERVICE) as
            ConnectivityManager).activeNetworkInfo != null


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun checkForPermission(permission: String, title: Int,
                                    description: Int, onPermissionResult: (Boolean) -> Unit) {
        this.onPermissionResult = onPermissionResult
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            onPermissionResult(true)
            return
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            alert(message = getString(description), title = getString(title)) {
                positiveButton(R.string.grant_permission) {
                    ActivityCompat.requestPermissions(
                            this@DestinationFinderActivity,
                            arrayOf(permission), REQUESTING_PERMISSION)
                }
                onCancelled {
                    onPermissionResult(false)
                }
            }.show()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(permission), REQUESTING_PERMISSION)
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean
            = when (item.itemId) {
        R.id.action_settings -> {
            startSettingsActivity()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun startSettingsActivity() {
        startActivityForResult(Intent(this, SettingsActivity::class.java), SETTINGS_ACTIVITY)
    }

    override fun showDatePicker(initialDate: Calendar, minDate: Calendar?, onComplete: (Calendar) -> Unit) {
        val initialYear = initialDate.get(Calendar.YEAR)
        val initialMonth = initialDate.get(Calendar.MONTH)
        val initialDay = initialDate.get(Calendar.DAY_OF_MONTH)
        val datePicker = DatePickerDialog(this,
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
        val timePicker = TimePickerDialog(this,
                TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    val newCalendar = Calendar.getInstance()
                    newCalendar.timeInMillis = initialDate.timeInMillis
                    newCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    newCalendar.set(Calendar.MINUTE, minute)
                    onComplete(newCalendar)
                }, initialHour, initialMinute, false)
        timePicker.show()
    }

    override fun startPlaceAutocompleteActivity() {
        try {
            val intent = PlacePicker.IntentBuilder()
                    .build(this)
            startActivityForResult(intent, PLACE_PICKER_REQUEST)
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
                    val place = PlacePicker.getPlace(this, data)
                    presenter.onDeparturePlaceSelected(place)
                }
                REQUESTING_PERMISSION -> onPermissionResult(true)
                SETTINGS_ACTIVITY -> presenter.reloadSettings()
                else -> {
                }
            }
        }
    }
}

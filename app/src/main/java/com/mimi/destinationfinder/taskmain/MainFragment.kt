package com.mimi.destinationfinder.taskmain

import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mimi.destinationfinder.R
import com.mimi.destinationfinder.base.BaseFragment
import com.mimi.destinationfinder.dto.Destination
import com.mimi.destinationfinder.utils.Context
import com.mimi.destinationfinder.utils.CurrentLocationUtil
import kotlinx.android.synthetic.main.fragment_main.*
import org.jetbrains.anko.support.v4.selector
import org.koin.android.ext.android.inject


/**
 * Created by Mimi on 17/11/2017.
 *
 */
class MainFragment : BaseFragment(), MainContract.View {
    override var isActive: Boolean = false
        get() = isAdded

    override val contextName = Context.DestinationFinder
    override val presenter: MainContract.Presenter by inject()


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_main, container, false)


    override fun onStart() {
        super.onStart()
        presenter.start()
    }

    override fun getMainActivity() = activity as MainContract.Activity

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


    override fun setDepartureTime(time: String) {
        departureTime.text = time
    }

    override fun showArrivalAddress(address: String) {
        arrivalPlace.post {
            arrivalPlace.text = address

            arrivalPlace.visibility = View.VISIBLE
        }
        changeArrivalLocation.post {
            changeArrivalLocation.visibility = View.VISIBLE
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
}

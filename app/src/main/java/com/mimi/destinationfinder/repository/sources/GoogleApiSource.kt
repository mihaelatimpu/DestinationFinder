package com.mimi.destinationfinder.repository.sources

import android.content.Context
import com.mimi.destinationfinder.dto.Location
import com.mimi.destinationfinder.dto.GooglePlace
import com.mimi.destinationfinder.dto.Requirements
import com.mimi.destinationfinder.repository.retrofit.requests.GetPlacesApi
import com.mimi.destinationfinder.repository.retrofit.FilterPlacesUtil
import com.mimi.destinationfinder.repository.retrofit.requests.TravelTimeApi
import com.mimi.destinationfinder.utils.GeocoderUtil
import io.reactivex.Observable
import io.reactivex.ObservableEmitter

/**
 * Created by Mimi on 18/11/2017.
 *
 */
class GoogleApiSource(override val data: Requirements) : BaseSource {
    private val converter = GeocoderUtil()
    private val filter = FilterPlacesUtil()

    override fun start(context: Context): Observable<Location>
        = Observable.create { e ->
            val initialObserver = GetPlacesApi(data).start(context = context)
            convertToList(initialObserver, { e.onError(it) }) {
                filterAndSortData(context, it, e)
            }
        }


    private fun filterAndSortData(context: Context,
                                  list: ArrayList<GooglePlace>,
                                  e: ObservableEmitter<Location>) {
        val withTravelTime = filter.calculateTravelTime(list,data)
        val bestPlaces = filter.getBestPlaces(withTravelTime, data)
        val destinations = bestPlaces.mapNotNull { converter.getAddress(context, it.geometry.location) }
        calculateExactTravelTime(destinations, e)
    }

    /**
     * calculate the exact travel time, based on google map api
     * @locations = the list of locations
     */
    private fun calculateExactTravelTime(locations: List<Location>,
                                         e: ObservableEmitter<Location>) {
        val observable = TravelTimeApi(data, locations).start()
        convertToList(observable, { e.onError(it) }) {
            it.forEach {
                e.onNext(it)
            }
            e.onComplete()
        }
    }

    private fun <T> convertToList(observable: Observable<T>,
                                  onError: (Throwable) -> Unit,
                                  onComplete: (ArrayList<T>) -> Unit) {
        val list = ArrayList<T>()
        observable.doOnError { onError(it) }
                .doOnNext { list.add(it) }
                .doOnComplete { onComplete(list) }
                .subscribe()
    }

}
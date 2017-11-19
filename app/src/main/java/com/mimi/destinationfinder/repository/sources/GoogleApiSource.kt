package com.mimi.destinationfinder.repository.sources

import android.content.Context
import com.mimi.destinationfinder.dto.Destination
import com.mimi.destinationfinder.dto.GooglePlace
import com.mimi.destinationfinder.dto.Requirements
import com.mimi.destinationfinder.repository.googleApi.requests.RequestGetPlaces
import com.mimi.destinationfinder.repository.googleApi.FilterPlacesUtil
import com.mimi.destinationfinder.repository.googleApi.requests.RequestTravelTime
import com.mimi.destinationfinder.utils.GeocoderUtil
import io.reactivex.Observable
import io.reactivex.ObservableEmitter

/**
 * Created by Mimi on 18/11/2017.
 *
 */
class GoogleApiSource(override val data: Requirements) : BaseSource {
    private val converter = GeocoderUtil()

    override fun start(context: Context): Observable<Destination> {
        return Observable.create { e ->
            val initialObserver = RequestGetPlaces(data).start(context = context)
            convertToList(initialObserver, { e.onError(it) }) {
                filterAndSortData(context, it, e)
            }
        }
    }

    private fun filterAndSortData(context: Context, list: ArrayList<GooglePlace>, e: ObservableEmitter<Destination>) {
        val sortedByScore = FilterPlacesUtil().filterPlaces(list, data)
        val destinations = sortedByScore.mapNotNull { converter.getAddress(context, it.geometry.location) }
        /*destinations.forEach {

            e.onNext(it)
        }
        e.onComplete()*/
        recalculateErrorsBasedOnGoogleMaps(destinations, e)
    }

    private fun recalculateErrorsBasedOnGoogleMaps(destinations: List<Destination>,
                                                   e: ObservableEmitter<Destination>) {
        val observable = RequestTravelTime(data, destinations).start()
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
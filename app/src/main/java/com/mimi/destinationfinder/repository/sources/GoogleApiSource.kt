package com.mimi.destinationfinder.repository.sources

import android.content.Context
import com.google.android.gms.tasks.OnSuccessListener
import com.mimi.destinationfinder.dto.Destination
import com.mimi.destinationfinder.dto.Event
import com.mimi.destinationfinder.dto.GooglePlace
import com.mimi.destinationfinder.dto.Requirements
import com.mimi.destinationfinder.repository.googleApi.requests.RequestAllAddresses
import com.mimi.destinationfinder.repository.googleApi.requests.RequestGetPlaces
import com.mimi.destinationfinder.repository.googleApi.FilterPlacesUtil
import com.mimi.destinationfinder.utils.GeocoderUtil
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe

/**
 * Created by Mimi on 18/11/2017.
 *
 */
class GoogleApiSource(override val data: Requirements) : BaseSource {
    /*override fun start(context: Context): Observable<Destination> {
        val initialObserver = RequestGetPlaces(data).start(context = context)
        val sortedByScore = FilterPlacesUtil().filterPlaces(initialObserver, data)
        return sortedByScore.flatMap {
            RequestAllAddresses(listOf(it)).start(context)
        }
    }*/
    private val converter = GeocoderUtil()

    override fun start(context: Context): Observable<Destination> {
        return Observable.create { e ->
            val list = ArrayList<GooglePlace>()
            val initialObserver = RequestGetPlaces(data).start(context = context)
            initialObserver.doOnError { e.onError(it) }
                    .doOnNext { list.add(it) }
                    .doOnComplete { onReceivedData(context, list, e) }
                    .subscribe()
        }
    }

    private fun onReceivedData(context: Context, list: ArrayList<GooglePlace>, e: ObservableEmitter<Destination>) {
        val sortedByScore = FilterPlacesUtil().filterPlaces(list, data)
        val destinations = sortedByScore.map { converter.getAddress(context, it.geometry.location) }
                .filter { it != null }
        destinations.forEach {

            e.onNext(it)
        }
        e.onComplete()
    }

}
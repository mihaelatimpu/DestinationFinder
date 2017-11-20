package com.mimi.destinationfinder.repository.sources

import android.content.Context
import com.mimi.destinationfinder.dto.Location
import com.mimi.destinationfinder.dto.Requirements
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Mimi on 18/11/2017.
 *
 */
class MainDestinationSource(override val data: Requirements,
                            private val sources: List<BaseSource>) : BaseSource {

    override fun start(context: Context): Observable<Location> {
        val observableList = sources.map { it.start(context) }
        return Observable.merge(observableList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

}
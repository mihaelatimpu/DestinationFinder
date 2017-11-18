package com.mimi.destinationfinder.repository.sources

import android.content.Context
import com.mimi.destinationfinder.dto.Destination
import com.mimi.destinationfinder.dto.Requirements
import io.reactivex.Observable

/**
 * Created by Mimi on 18/11/2017.
 *
 */
class MainDestinationSource(override val data: Requirements,
                            val sources: List<BaseSource>) : BaseSource {

    override fun start(context: Context): Observable<Destination> {
        val observableList = sources.map { it.start(context) }
        return Observable.merge(observableList)
    }

}
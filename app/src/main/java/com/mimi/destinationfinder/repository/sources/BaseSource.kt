package com.mimi.destinationfinder.repository.sources

import android.content.Context
import com.mimi.destinationfinder.dto.Destination
import com.mimi.destinationfinder.dto.Requirements
import io.reactivex.Observable
import java.util.*

/**
 * Created by Mimi on 18/11/2017.
 * The interface from sources of addresses
 */
interface BaseSource {
    val data:Requirements
    fun start(context: Context):Observable<Destination>
}

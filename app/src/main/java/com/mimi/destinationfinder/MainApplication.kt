package com.mimi.destinationfinder

import android.app.Application
import com.mimi.destinationfinder.utils.appModules
import org.koin.android.ext.android.startAndroidContext

/**
 * Created by Mimi on 17/11/2017.
 *
 */

class MainApplication : Application(){

    override fun onCreate() {
        super.onCreate()

        // Start Koin
        startAndroidContext(this, appModules())
    }
}
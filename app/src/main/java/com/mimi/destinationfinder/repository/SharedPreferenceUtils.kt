package com.mimi.destinationfinder.repository

import android.content.Context
import com.mimi.destinationfinder.dto.Radius
import com.mimi.destinationfinder.dto.ResultsCount
import com.mimi.destinationfinder.dto.Settings
import com.mimi.destinationfinder.dto.TransportMode

/**
 * Created by Mimi on 19/11/2017.
 *
 */
object SharedPreferenceUtils {
    private val PREF_FILENAME = "settingsPreferences"
    private val RADIUS = "radius"
    private val TRANSPORT_MODE = "transport_mode"
    private val MAX_RESULTS = "max_results"
    private val PLACES_OF_INTERES = "placesOfInterest"

    fun saveSettings(context: Context, settings: Settings) {
        val prefs = context.getSharedPreferences(PREF_FILENAME, 0)
        val editor = prefs.edit()
        editor.putInt(RADIUS, settings.radius.type)
        editor.putInt(TRANSPORT_MODE, settings.transportMode.type)
        editor.putInt(MAX_RESULTS, settings.maxResultPerCount.type)
        editor.putStringSet(PLACES_OF_INTERES, settings.placesOnInterests.toSet())
        editor.apply()
    }

    fun retrieveSettings(context: Context):Settings {
        val prefs = context.getSharedPreferences(PREF_FILENAME, 0)
        val settings = Settings.default()
        with(settings) {
            radius = Radius(prefs.getInt(RADIUS, radius.type))
            transportMode = TransportMode(prefs.getInt(TRANSPORT_MODE, transportMode.type))
            maxResultPerCount = ResultsCount(prefs.getInt(MAX_RESULTS,
                    maxResultPerCount.type))
            placesOnInterests = prefs.getStringSet(PLACES_OF_INTERES,
                    placesOnInterests.toSet()).toList()
        }
        return settings
    }
}
package com.mimi.destinationfinder.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Mimi on 17/11/2017.
 *
 */
class TimeConverter{
    companion object {
        val REGULAR_DATE_PATTERN = "MMM d, YYYY"
        val REGULAR_TIME_PATTERN = "hh:mm a"
    }
    fun toRegularDateString(calendar: Calendar = Calendar.getInstance()):String{
        val sdf = SimpleDateFormat(REGULAR_DATE_PATTERN, Locale.ENGLISH)
        return sdf.format(calendar.time)
    }
    fun toRegularTimeString(calendar: Calendar = Calendar.getInstance()):String{
        val sdf = SimpleDateFormat(REGULAR_TIME_PATTERN, Locale.ENGLISH)
        return sdf.format(calendar.time)
    }
}
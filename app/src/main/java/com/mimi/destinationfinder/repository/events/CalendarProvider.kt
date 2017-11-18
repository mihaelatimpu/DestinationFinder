package com.mimi.destinationfinder.repository.events

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.provider.CalendarContract
import com.mimi.destinationfinder.dto.Event
import java.util.*

/**
 * Created by Mimi on 17/11/2017.
 *
 */
class CalendarProvider {
    companion object {
        val PROJECTION_ID_INDEX = 0
        val PROJECTION_BEGIN_INDEX = 1
        val PROJECTION_END_INDEX = 2
        val PROJECTION_TITLE_INDEX = 3
        val PROJECTION_LOCATION_INDEX = 4
        val INSTANCE_PROJECTION = arrayOf(
                CalendarContract.Instances.EVENT_ID,
                CalendarContract.Instances.BEGIN,
                CalendarContract.Instances.END,
                CalendarContract.Instances.TITLE,
                CalendarContract.Instances.EVENT_LOCATION
        )
    }

    fun getEvents(beginTime: Calendar, endTime: Calendar, cr: ContentResolver):List<Event> {
        val builder = CalendarContract.Instances.CONTENT_URI.buildUpon()
        ContentUris.appendId(builder, beginTime.timeInMillis)
        ContentUris.appendId(builder, endTime.timeInMillis)
        val cur = cr.query(builder.build(),
                INSTANCE_PROJECTION,
                "",
                arrayOf(),
                null)
        val events = arrayListOf<Event>()
        while (cur.moveToNext()) {
            events.add(getEventFromCursor(cur))
        }
        cur.close()
        return events
    }

    private fun getEventFromCursor(cur: Cursor): Event {
        val eventID = cur.getLong(PROJECTION_ID_INDEX)
        val beginTime = cur.getLong(PROJECTION_BEGIN_INDEX)
        val endTime = cur.getLong(PROJECTION_END_INDEX)
        val title = cur.getString(PROJECTION_TITLE_INDEX)
        val location = cur.getString(PROJECTION_LOCATION_INDEX)
        return Event(eventId = eventID,
                title = title,
                eventEndTime = endTime,
                eventStartTime = beginTime,
                location = location)
    }

}
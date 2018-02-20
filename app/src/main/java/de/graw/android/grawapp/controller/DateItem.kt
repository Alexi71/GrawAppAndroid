package de.graw.android.grawapp.controller

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

class  DateItem {
    companion object {
        fun getFirstDayOfWeek(format:String = "dd/MM/yyyy") :String {
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, 0) // ! clear would not reset the hour of day !
            cal.clear(Calendar.MINUTE)
            cal.clear(Calendar.SECOND)
            cal.clear(Calendar.MILLISECOND)

            cal.set(Calendar.DAY_OF_WEEK,cal.firstDayOfWeek)
            val df = SimpleDateFormat(format)
            val dateString = df.format(cal.time)
            Log.i("test",dateString)
            return dateString
        }

        fun getLastDayOfWeek(format:String = "dd/MM/yyyy") :String {
            val cal = Calendar.getInstance()
            val lastDayOfWeek = lastDayOfWeek(cal)
            val df = SimpleDateFormat(format)
            val dateString = df.format(lastDayOfWeek.time)
            Log.i("test",dateString)
            return dateString
        }

        fun lastDayOfWeek(calendar: Calendar): Calendar {
            val cal = calendar.clone() as Calendar
            var day = cal.get(Calendar.DAY_OF_YEAR)
            while (cal.get(Calendar.DAY_OF_WEEK) !== Calendar.SATURDAY) {
                cal.set(Calendar.DAY_OF_YEAR, ++day)
            }
            return cal
        }
    }
}

package de.graw.android.grawapp.helper.Settings

import android.content.Context
import android.content.SharedPreferences
import de.graw.android.grawapp.controller.DateItem
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

/**
 * Created by KotikA on 19.02.2018.
 */
class Settings(val context: Context) {
    val PREFS_FILENAME = "de.graw.android.grawapp.prefs"
    val PREF_FIRST_DATE = "de.graw.android.grawapp.prefs.firstdate"
    val PREF_LAST_DATE = "de.graw.android.grawapp.prefs.lastdate"
    /*val PREF_USERNAME = "de.graw.android.grawapp.prefs.username"
    val PREF_PASSWORD = "de.graw.android.grawapp.prefs.password"*/

    var prefs: SharedPreferences? = context.getSharedPreferences(PREFS_FILENAME,0)

    fun saveFlightDates(firstDate:DateTime, lastDate:DateTime) {
        val edit = prefs!!.edit()
        edit.putString(PREF_FIRST_DATE,firstDate.toString(DateTimeFormat.forPattern("dd/MM/yyyy")))
        edit.putString(PREF_LAST_DATE,lastDate.toString(DateTimeFormat.forPattern("dd/MM/yyyy")))
        edit.apply()
    }

    fun getFirstDay() :DateTime {
        val firstDateString = prefs!!.getString(PREF_FIRST_DATE,"")
        val formatter = DateTimeFormat.forPattern("dd/MM/yyyy")
        if(!firstDateString.isNullOrEmpty())
        {
            val datetime = formatter.parseDateTime(firstDateString)
            return datetime
        }
        else {
            return formatter.parseDateTime(DateItem.getFirstDayOfWeek())
        }
    }

    fun getLastDay():DateTime {
        val lastDateString = prefs!!.getString(PREF_LAST_DATE,"")
        val formatter = DateTimeFormat.forPattern("dd/MM/yyyy")
        if(!lastDateString.isNullOrEmpty())
        {
            val datetime = formatter.parseDateTime(lastDateString)
            return datetime
        }
        else {
            return formatter.parseDateTime(DateItem.getLastDayOfWeek())
        }
    }



}
package de.graw.android.grawapp.dataBase

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import org.jetbrains.anko.db.*

class DatabaseOpenHelper(context: Context ):ManagedSQLiteOpenHelper(context,databaseValues.DATABASE_NAME,null,1){

    companion object {
        private var instance: DatabaseOpenHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): DatabaseOpenHelper {
            if (instance == null) {
                instance = DatabaseOpenHelper(ctx.getApplicationContext())
            }
            return instance!!
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db!!.dropTable(FlightTable.TABLE_NAME, true)
    }

    override fun onCreate(db: SQLiteDatabase?) {
       db!!.createTable(FlightTable.TABLE_NAME,true,
               FlightTable.ID to INTEGER + PRIMARY_KEY+ AUTOINCREMENT+ UNIQUE,
               FlightTable.TEMPERATURE to REAL,
               FlightTable.PRESSURE to REAL,
               FlightTable.HUMIDITY to REAL,
               FlightTable.WINSPEED to REAL,
               FlightTable.WINDDIRECTION to REAL,
               FlightTable.ALTITUDE to REAL,
               FlightTable.GEOPOTENTIAL to REAL,
               FlightTable.LONGITUDE to REAL,
               FlightTable.LATITUDE to REAL,
               FlightTable.DEWPOINT to REAL,
               FlightTable.FLIGHT_ID to TEXT)
    }

    val Context.database: DatabaseOpenHelper
        get() = getInstance(applicationContext)

}

object FlightTable:BaseColumns {
    val TABLE_NAME = "FlightProfileData"
    val ID = "_id"
    val TIME = "time"
    val TEMPERATURE = "temperature"
    val PRESSURE = "pressure"
    val HUMIDITY = "humidity"
    val WINSPEED ="windspeed"
    val WINDDIRECTION = "winddirection"
    val ALTITUDE = "altitude"
    val GEOPOTENTIAL = "geopotential"
    val LONGITUDE = "longitude"
    val LATITUDE = "latitude"
    val DEWPOINT = "dewpoint"
    val FLIGHT_ID = "flight_id"
}





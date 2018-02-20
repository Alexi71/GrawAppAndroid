package de.graw.android.grawapp.dataBase

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log
import android.widget.FrameLayout

class DbHelper(context:Context):SQLiteOpenHelper(context,
        databaseValues.DATABASE_NAME,
        null,
        databaseValues.DATABASE_VERSION) {

    val userTable = "CREATE TABLE user (_id INTEGER PRIMARY KEY,name VARCHAR (50),key_value VARCHAR (100));"
    val stationTable ="CREATE TABLE station (_id INTEGER PRIMARY KEY,station_id VARCHAR (50),  name VARCHAR (50),city      VARCHAR (50),country   VARCHAR (50),longitude VARCHAR (50),latitude  VARCHAR (50),altitude  VARCHAR (50),key_value VARCHAR (100));"
    val userStationtable = "CREATE TABLE user_station (_id INTEGER PRIMARY KEY,is_default INTEGER,user_id INTEGER REFERENCES users (_id),station_id INTEGER REFERENCES station (_id),FOREIGN KEY (station_id)REFERENCES station (_id));"
    val flightData = "CREATE TABLE ${FlightTable.TABLE_NAME} ( " +
    "${FlightTable.ID}           INTEGER      PRIMARY KEY," +
            "${FlightTable.TIME}   DOUBLE,"+
    "${FlightTable.TEMPERATURE}   DOUBLE,"+
    "${FlightTable.PRESSURE}      DOUBLE,"+
    "${FlightTable.HUMIDITY}       DOUBLE,"+
    "${FlightTable.WINSPEED}     DOUBLE,"+
    "${FlightTable.WINDDIRECTION} DOUBLE,"+
    "${FlightTable.ALTITUDE}      DOUBLE,"+
    "${FlightTable.GEOPOTENTIAL}  DOUBLE,"+
    "${FlightTable.LONGITUDE}     DOUBLE,"+
    "${FlightTable.LATITUDE}      DOUBLE,"+
    "${FlightTable.DEWPOINT}      DOUBLE,"+
    "${FlightTable.FLIGHT_ID}     STRING (100));"
    override fun onCreate(database: SQLiteDatabase) {
        database.execSQL(userTable)
        database.execSQL(stationTable)
        database.execSQL(userStationtable)
        database.execSQL(flightData)
        Log.i("test","Table created successfully")

    }

    override fun onUpgrade(database: SQLiteDatabase, p1: Int, p2: Int) {
        database.execSQL("DROP TABLE IF EXISTS ${databaseValues.USER_STATION_TABLE}")
        database.execSQL("DROP TABLE IF EXISTS ${databaseValues.USER_TABLE}")
        database.execSQL("DROP TABLE IF EXISTS ${databaseValues.STATION_TABLE}")
        database.execSQL("DROP TABLE IF EXISTS ${FlightTable.TABLE_NAME}")
        database.execSQL(userTable)
        database.execSQL(stationTable)
        database.execSQL(userStationtable)
        database.execSQL(flightData)
        Log.i("test","Table updated successfully")
    }

}

object databaseValues:BaseColumns {
    val DATABASE_NAME = "grawdb"
    val USER_TABLE = "user"
    val STATION_TABLE = "station"
    val USER_STATION_TABLE = "user_station"
    val ID_COLUMN = "_id"
    val NAME_COLUMN = "name"
    val KEY_COLUMN = "key_value"
    val CITY_COLUMN = "city"
    val COUNTRY_COLUMN = "country"
    val LONGITUDE_COLUMN = "longitude"
    val LATITUDE_COLUMN = "latitude"
    val ALTITUDE_COLUMN = "altitude"
    val STATION_ID_COLUMN = "station_id"
    val IS_DEFAULT_COLUMN = "is_default"
    val USER_STATION_USER_ID = "user_id"
    val USER_STATION_STATION_ID = "station_id"
    val DATABASE_VERSION = 4

}

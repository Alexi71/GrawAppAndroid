package de.graw.android.grawapp.dataBase

import android.content.Context
import android.util.Log
import de.graw.android.grawapp.model.InputData
import org.jetbrains.anko.db.insert

class DbFlightDataManager(context: Context) {
    var db = DatabaseOpenHelper(context)

     fun insertInputData(dataList:List<InputData>,flightKey:String):Boolean {
        var result = true

         //data already inserted??
         if(getCount(flightKey) > 0) {
             return result
         }
        db.use {
            dataList.forEach {
                try {
                    beginTransaction()
                    val id = insert(FlightTable.TABLE_NAME, FlightTable.TEMPERATURE to it.Temperature,
                            FlightTable.PRESSURE to it.Pressure,
                            FlightTable.HUMIDITY to it.Humidity,
                            FlightTable.WINSPEED to it.WindSpeed,
                            FlightTable.WINDDIRECTION to it.WindDirection,
                            FlightTable.ALTITUDE to it.Altitude,
                            FlightTable.GEOPOTENTIAL to it.Geopotential,
                            FlightTable.LONGITUDE to it.Longitude,
                            FlightTable.LATITUDE to it.Latitude,
                            FlightTable.DEWPOINT to it.DewPoint,
                            FlightTable.FLIGHT_ID to flightKey)

                    Log.i("test", "flight added ${id}")
                }
                catch (e:Exception) {
                    Log.i("test",e.localizedMessage)
                    result = false
                }
                finally {
                    endTransaction()
                }
            }

        }
        return result
    }

    fun getCount(flightKey:String):Int {
        var count:Int = 0
        val quoteTypeCount = db.use {
            var cursor = rawQuery("select count(*) AS returnCount FROM ${FlightTable.TABLE_NAME}",null)
            if(cursor != null) {
                cursor.moveToFirst()
                count = cursor.count
            }
            //
        }
        return count
    }

 }

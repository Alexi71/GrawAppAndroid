package de.graw.android.grawapp.dataBase

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log
import de.graw.android.grawapp.model.StationItem
import de.graw.android.grawapp.model.UserItem
import java.util.ArrayList

class TableHelper(context: Context){

    private val helper = DbHelper(context)

    fun saveUser(user: UserItem):Long {
        val db = helper.writableDatabase
        var content = ContentValues()
        content.put(databaseValues.NAME_COLUMN,user.name)
        content.put(databaseValues.KEY_COLUMN,user.key)
        val userId = getUserId(user)
        if(userId == null) {
            db.beginTransaction()
            val id: Long = try {
                val id = db.insert(databaseValues.USER_TABLE, null, content)
                db.setTransactionSuccessful()
                id
            } finally {
                db.endTransaction()
                db.close()
            }
            return id
        }
        return userId.toLong()
    }

    fun saveStation(item: StationItem) :Long {
        val db = helper.writableDatabase
        var content = ContentValues()
        content.put(databaseValues.NAME_COLUMN, item.name)
        content.put(databaseValues.KEY_COLUMN, item.key)
        content.put(databaseValues.CITY_COLUMN, item.city)
        content.put(databaseValues.COUNTRY_COLUMN, item.country)
        content.put(databaseValues.LONGITUDE_COLUMN, item.longitude)
        content.put(databaseValues.LATITUDE_COLUMN, item.latitude)
        content.put(databaseValues.ALTITUDE_COLUMN, item.altitude)
        content.put(databaseValues.STATION_ID_COLUMN, item.id)
        val stationId = getStationId(item)

        if (stationId != null) {
            return stationId.toLong()
        }

        db.beginTransaction()
        val id: Long = try {
            val id = db.insert(databaseValues.STATION_TABLE, null, content)
            db.setTransactionSuccessful()
            id
        } finally {
            db.endTransaction()
            db.close()
            Log.i("test","station ${item.name} successfully added to db")
        }
        return id

    }

    fun setDefaultStation(user: UserItem, station: StationItem) :Long? {
        val db = helper.writableDatabase

        val userId = getUserId(user)
        val stationId = getStationId(station)
        if(userId != null && stationId != null){
            val cursor = db.rawQuery("select * from user_station where user_id = ${userId} and station_id = ${stationId}",null)
            val count = cursor.count
            if(count > 0 ) {
                //Update station first set all user station as not default
               try {
                   var cursor = db.rawQuery("UPDATE ${databaseValues.USER_STATION_TABLE} set ${databaseValues.IS_DEFAULT_COLUMN} = 0",null)
                   cursor.moveToFirst()
                   cursor.close()

                   cursor = db.rawQuery("UPDATE ${databaseValues.USER_STATION_TABLE} set ${databaseValues.IS_DEFAULT_COLUMN} = 0 where user_id = '${userId}' and station_id = '${stationId}'",null)
                   cursor.moveToFirst()
                   cursor.close()
                   val id = getUserStationId(userId,stationId)
                   return id?.toLong()

               }
               catch (e:Exception) {
                   e.printStackTrace()
               }

            }
            else {
                val id = saveUserStation(userId,stationId,1)
                Log.i("test","Save default station with userid:${userId} statuinId:${stationId}")
                return id
            }
        }

        return null

    }

    fun saveUserStation(userID:Int,stationId:Int,isDefault:Int) :Long{
        val db = helper.writableDatabase
        var content = ContentValues()
        content.put(databaseValues.USER_STATION_USER_ID,userID)
        content.put(databaseValues.USER_STATION_STATION_ID,stationId)
        content.put(databaseValues.IS_DEFAULT_COLUMN,isDefault)

        db.beginTransaction()
        val id:Long = try {
            val id = db.insert(databaseValues.USER_STATION_TABLE,null,content)
            db.setTransactionSuccessful()
            id
        }
        finally {
            db.endTransaction()
            db.close()
        }
        return id
    }

    fun getUserId(user: UserItem) :Int?{
        val db = helper.readableDatabase
        var cursor:Cursor? = null
        try {
            cursor = db.rawQuery("select _id from user where key_value = '${user.key}'",null)
        }
        catch (e:Exception) {
            e.printStackTrace()
        }

        if(cursor?.count?:0 > 0) {
            cursor!!.moveToFirst()
            val id = cursor!!.getInt(cursor!!.getColumnIndex(databaseValues.ID_COLUMN))
            return id
        }
      return null
    }

    fun getStationId(station: StationItem) :Int?{
        val db = helper.readableDatabase
        var cursor:Cursor? = null
        try {
             cursor = db.rawQuery("select _id from station where key_value = '${station.key}'",null)
        }
        catch (e:Exception) {
            e.printStackTrace()
        }

        if(cursor!!.count > 0) {
            cursor!!.moveToFirst()
            val id = cursor!!.getInt(cursor!!.getColumnIndex(databaseValues.ID_COLUMN))
            return id
        }
        return null
    }

    fun getUserStationId (userId:Int,stationId:Int) :Int?{
        val db = helper.readableDatabase
        var cursor:Cursor? = null
        try {
            cursor = db.rawQuery("select _id from ${databaseValues.USER_STATION_TABLE} where user_id = ${userId} and station_id =${stationId}",null)
        }
        catch (e:Exception) {
            e.printStackTrace()
        }

        if(cursor!!.count > 0) {
            cursor!!.moveToFirst()
            val id = cursor!!.getInt(cursor!!.getColumnIndex(databaseValues.ID_COLUMN))
            return id
        }
        return null
    }

    fun getDefaultStation(user: UserItem): StationItem?{
        val db = helper.readableDatabase
        var cursor:Cursor? = null
        try {
            val uid = getUserId(user)
            if(uid != null) {
                cursor = db.rawQuery("select * from ${databaseValues.USER_STATION_TABLE} where ${databaseValues.USER_STATION_USER_ID} = ${uid} and ${databaseValues.IS_DEFAULT_COLUMN} = 1",null)
            }
            else {
                Log.i("test","No user found for default station")
                return  null
            }

        }
        catch (e:Exception) {
            e.printStackTrace()
        }

        if(cursor!!.count > 0) {
            cursor!!.moveToFirst()
            val id = cursor!!.getInt(cursor!!.getColumnIndex(databaseValues.USER_STATION_STATION_ID))
            val item = getStation(id)
            return item
        }
        return null
    }

    fun getStation(id:Int): StationItem? {
        val db = helper.readableDatabase
        var cursor:Cursor? = null
        try {
            cursor = db.rawQuery("select * from ${databaseValues.STATION_TABLE} where ${databaseValues.ID_COLUMN} = '${id}' ",null)
        }
        catch (e:Exception) {
            e.printStackTrace()
        }

        if(cursor!!.count > 0) {
            cursor!!.moveToFirst()
            val stationId = cursor!!.getString(cursor!!.getColumnIndex(databaseValues.STATION_ID_COLUMN))
            val name = cursor!!.getString(cursor!!.getColumnIndex(databaseValues.NAME_COLUMN))
            val city = cursor!!.getString(cursor!!.getColumnIndex(databaseValues.CITY_COLUMN))
            val country = cursor!!.getString(cursor!!.getColumnIndex(databaseValues.COUNTRY_COLUMN))
            val keyValue = cursor!!.getString(cursor!!.getColumnIndex(databaseValues.KEY_COLUMN))
            val altitude = cursor!!.getString(cursor!!.getColumnIndex(databaseValues.ALTITUDE_COLUMN))
            val lon = cursor!!.getString(cursor!!.getColumnIndex(databaseValues.LONGITUDE_COLUMN))
            val lat = cursor!!.getString(cursor!!.getColumnIndex(databaseValues.LATITUDE_COLUMN))
            val item = StationItem(stationId, name, city, country, lon, lat, altitude, keyValue)
            return item
        }
        return null
    }

    fun getUserSubscribedStation(userItem: UserItem) :List<StationItem> {
        val db = helper.readableDatabase
        var cursor:Cursor? = null
        var stationList = ArrayList<StationItem>()

        var userId = getUserId(userItem)
        if(userId == null) {
            return stationList
        }

        try {
            cursor = db.rawQuery("select * from ${databaseValues.USER_STATION_TABLE} where ${databaseValues.USER_STATION_USER_ID} = ${userId}", null)
        }
        catch (e:Exception) {
            e.printStackTrace()
        }

        if(cursor!!.count > 0) {
            cursor!!.moveToFirst()
            do {
                val id = cursor!!.getInt(cursor!!.getColumnIndex(databaseValues.USER_STATION_STATION_ID))
                val item = getStation(id)
                stationList.add(item!!)
            }while (cursor!!.isAfterLast)
        }
        return stationList
    }

}

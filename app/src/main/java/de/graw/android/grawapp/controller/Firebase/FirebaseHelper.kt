package de.graw.android.grawapp.controller.Firebase

import android.util.Log
import com.google.firebase.database.*
import de.graw.android.grawapp.model.RawData
import java.io.Serializable


class FirebaseHelper(dataListener:FirebaseSnapshotListener?):Serializable {
    private val database = FirebaseDatabase.getInstance()
    private var ref :DatabaseReference? = null
    private var listener:FirebaseSnapshotListener? = null
    private var childListener:ChildEventListener? = null
    private val listenerList = ArrayList<FirebaseSnapshotListener>()

    constructor():this(null)
    init {
        this.listener = dataListener
        listenerList.add(this.listener!!)
    }
    fun startRawDataListener(stationKey:String,flightKey:String) {
        ref = database.getReference().child("station").child(stationKey).child("flights").child(flightKey)
        childListener = object :ChildEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                Log.i("test","snapshot error")
            }

            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
                Log.i("test","snapshot moved")
            }

            override fun onChildChanged(data: DataSnapshot?, p1: String?) {
                Log.i("test","snapshot changed")
            }

            override fun onChildAdded(data: DataSnapshot?, p1: String?) {
                Log.i("test", "snapshot added ${data!!.key}")
                if(data.exists()) {
                    if(data!!.key == "rawdata") {
                        val map = data.getValue() as Map<String, Any>
                        val keysets = map.keys.toMutableList()
                        val valueMap = map.getValue(keysets[0])as Map<String, Any>

                        val rawdata = RawData()
                        rawdata.epochTime = valueMap.getValue("EpochTime") as Double
                        rawdata.temperature = valueMap.getValue("Temperature") as Double
                        rawdata.pressure = valueMap.getValue("Pressure") as Double
                        rawdata.humdity = valueMap.getValue("Humidity") as Double
                        rawdata.windspeed = valueMap.getValue("WindSpeed") as Double
                        rawdata.winddirection = valueMap.getValue("WinDirection") as Double
                        rawdata.longitude = valueMap.getValue("Longitude") as Double
                        rawdata.latitude = valueMap.getValue("Latitude") as Double
                        rawdata.altitude = valueMap.getValue("Altitude") as Double
                        listenerList.forEach {
                            if(it != null) {
                                it.onSetData(rawdata)
                            }
                        }

                        //Log.i("test", "stop here")
                    }
                }

            }

            override fun onChildRemoved(data: DataSnapshot?) {
                Log.i("test","snapshot removed")
            }
        }
        ref?.addChildEventListener(childListener)
    }

    fun stopRawDataListener() {
        ref?.removeEventListener(childListener)
    }

    fun addListener(listener: FirebaseSnapshotListener) {
        listenerList.add(listener)
    }

    fun removeListener(listener: FirebaseSnapshotListener) {
        listenerList.remove(listener)
    }
}

interface FirebaseSnapshotListener {
    fun onSetData(data:RawData)

}

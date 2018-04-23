package de.graw.android.grawapp.controller.Firebase

import android.util.Log
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import de.graw.android.grawapp.controller.InputDataController
import de.graw.android.grawapp.dataBase.TableHelper
import de.graw.android.grawapp.model.RawData
import org.jetbrains.anko.doAsync
import java.io.File
import java.io.Serializable


class FirebaseHelper(dataListener:FirebaseSnapshotListener?):Serializable {
    private val database = FirebaseDatabase.getInstance()
    private var ref :DatabaseReference? = null
    var listener:FirebaseSnapshotListener? = null
    private var childListener:ChildEventListener? = null
    private val listenerList = ArrayList<FirebaseSnapshotListener>()
    private val dataLister = ArrayList<FireBaseInputControllerFromUrlListener>()

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
                        rawdata.sensorStatus = valueMap.getValue("SensorStatus") as Long
                        rawdata.telemetryStatus = valueMap.getValue("TelemetryStatus") as Long
                        rawdata.gpsStatus = valueMap.getValue("GpsStatus") as Long
                        rawdata.startDetected = valueMap.getValue("StartDetected") as Boolean
                        rawdata.startTime = valueMap.getValue("StartTimeEpoch") as Double
                        if(valueMap.containsKey("Url")) {
                            rawdata.url = valueMap.get("Url") as String
                        }
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

    fun addDataListener(listener: FireBaseInputControllerFromUrlListener) {
        dataLister.add(listener)
    }

    fun removeDataListener(listener: FireBaseInputControllerFromUrlListener) {
        dataLister.remove(listener)
    }

    fun loadData(url:String) {

        val storage = FirebaseStorage.getInstance()
        val ref = storage.getReferenceFromUrl(url)
        //var inputDataController = InputDataController()
        val localFile: File = File.createTempFile("data", "json")
        ref.getFile(localFile)
                .addOnSuccessListener { taskSnapshot ->
                    // Local temp file has been created
                    // use localFile
                    var inputStream = localFile.inputStream()
                    var inputDataController = InputDataController()
                    inputDataController!!.getDataFromJson(inputStream)

                    dataLister.forEach {
                        if(it != null) {
                            it.onSetInputController(inputDataController)
                        }
                    }
                    //listener?.onSetInputController(inputDataController)
                    /*if(inputDataController!!.dataList != null) {
                        doAsync {
                            val db = TableHelper(context)
                            db.insertFlightData(inputDataController!!.dataList!!, flightData!!.key)
                        }
                        loadChartFragment()
                    }*/

                    /* val gson = Gson()
                     val jsonStream = InputStreamReader(inputStream)

 // Create a buffered reader
                     val bufferedReader = BufferedReader(jsonStream)
                     val result:List<InputData> = gson.fromJson(bufferedReader,Array<InputData>::class.java).toList()

                     Log.i("test","${result.size}")*/


                    /*async(UI) {
                         var result = bg{convertStreamToString(inputStream)}
                         parseJson( result.await())
                     }*/



                    //var json = JSONObject(localFile)

                }
                .addOnFailureListener { exception ->
                    // Handle any errors
                }
                .addOnProgressListener { taskSnapshot ->
                    // taskSnapshot.bytesTransferred
                    // taskSnapshot.totalByteCount
                    Log.i("test","bytes transfered ${taskSnapshot.bytesTransferred}")
                }
        //return inputDataController
    }
}

interface FirebaseSnapshotListener {
    fun onSetData(data:RawData)


}
interface FireBaseInputControllerFromUrlListener {
    fun onSetInputController(inputDataController: InputDataController)
}

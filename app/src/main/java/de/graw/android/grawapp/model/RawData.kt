package de.graw.android.grawapp.model

data class RawData(var epochTime:Double, var pressure:Double, var temperature:Double,
                   var humdity:Double, var windspeed:Double, var winddirection:Double,
                   var latitude:Double, var longitude:Double, var altitude:Double,
                   var sensorStatus:Long,var telemetryStatus:Long,var gpsStatus:Long,
                   var startDetected:Boolean,var startTime:Double,var url:String) {
    constructor():this(0.0,0.0,0.0,0.0,0.0,0.0,

            0.0,0.0,0.0,2,2,2,
            false,0.0,"")
}


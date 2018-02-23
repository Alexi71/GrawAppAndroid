package de.graw.android.grawapp.model

data class RawData(var epochTime:Double, var pressure:Double, var temperature:Double,
                   var humdity:Double, var windspeed:Double, var winddirection:Double,
                   var latitude:Double, var longitude:Double, var altitude:Double) {
    constructor():this(0.0,0.0,0.0,0.0,0.0,0.0,

            0.0,0.0,0.0)
}


package de.graw.android.grawapp.model

import java.io.Serializable

data class InputData(var Time:Double,var DisplayTime:String, var Temperature:Double,
                     var Pressure:Double,var Humidity:Double,var  WindSpeed:Double,
                     var WindDirection:Double,var Altitude:Double,var Geopotential:Double,
                     var Longitude:Double,var Latitude:Double,var DewPoint:Double,
                     var RefractionIndex:Double,var ModifiedRefractionIndex:Double,
                     var Elevation:Double,var Azimut:Double,var Range:Double,
                     var LocalOzon:Double,var AirDensity:Double,var RisingSpeed:Double,
                     var VirtualTemperature:Double,var Current:Double,
                     var BoxTemperature:Double,var TotalOzone:Double, var PumpCurrent:Double,
                     var PumpVoltage:Double):Serializable {
    constructor():this(0.0,"0",0.0,0.0,0.0,0.0,0.0,0.0,
            0.0,0.0,0.0,0.0,0.0,0.0,0.0,
            0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,
            0.0,0.0,0.0){

    }
}
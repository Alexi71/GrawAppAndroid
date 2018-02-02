package de.graw.android.grawapp.model

import java.io.Serializable

data class FlightData (
    var key:String,
    var date:String,
    var time:String,
    var fileName :String,
    var url:String ,
    var url100:String ,
    var urlEnd:String,
    var isRealTimeData:Boolean):Serializable
{
    constructor():this("","","","","","","",false)
}
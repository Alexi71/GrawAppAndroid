package de.graw.android.grawapp

data class FlightData (
    var key:String,
    var date:String,
    var time:String,
    var fileName :String,
    var url:String ,
    var url100:String ,
    var urlEnd:String,
    var isRealTimeData:Boolean)
{
    constructor():this("","","","","","","",false)
}
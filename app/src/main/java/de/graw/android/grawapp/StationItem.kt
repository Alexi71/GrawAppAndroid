package de.graw.android.grawapp

import java.io.Serializable

data class StationItem (
    var id :String ,
    var name :String,
    var city: String ,
    var country:String ,
    var longitude : String ,
    var latitude : String ,
    var altitude :String ,
    var key :String ):Serializable {

    constructor() :this("","","","","","","","")

}




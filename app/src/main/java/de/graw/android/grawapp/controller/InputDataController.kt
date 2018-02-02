package de.graw.android.grawapp.controller

import android.util.Log
import com.google.gson.Gson
import de.graw.android.grawapp.model.InputData
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class InputDataController {


    var dataList :List<InputData>? = null

    fun getDataFromJson(stream:InputStream) {
        val gson = Gson()
        val jsonStream = InputStreamReader(stream)
        val bufferedReader = BufferedReader(jsonStream)
        dataList = gson.fromJson(bufferedReader,Array<InputData>::class.java).toList()
        Log.i("test","${dataList!!.size}")
    }

}

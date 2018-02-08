package de.graw.android.grawapp.controller.chart

import android.provider.BaseColumns
import android.transition.ChangeTransform
import android.util.Log
import com.syncfusion.charts.ChartDataPoint
import de.graw.android.grawapp.model.InputData
import org.jetbrains.anko.collections.forEachByIndex

class InputSeriesData(val data: ArrayList<InputData>,val argumentType:ArgumentType,val valueType:ValueType) {

    private var inputData:ArrayList<InputData>
    var dataList  = ArrayList<ChartDataPoint<Double,Double>>()

    init {
        this.inputData = data
    }

     fun initialize(isFiltered:Boolean = false) {
        var result:List<InputData> = this.inputData
         if(isFiltered) {
             result = this.inputData.filter { s-> (s.Time % 600 == 0.0) }
         }
        result.forEach {
            dataList.add(ChartDataPoint(getArgument(it,argumentType),getValue(it,valueType)))
        }
         Log.i("test","Chart series init done ${dataList.size}")
    }

    private fun getValue(data:InputData, valueType: ValueType ) :Double {
        when (valueType) {
            ValueType.PRESSURE -> return data.Pressure
            ValueType.TEMPERATURE-> return  data.Temperature
            ValueType.HUMIDITY -> return data.Humidity
            ValueType.ALTITUDE -> return data.Altitude
            ValueType.DEWPOINT-> return  data.DewPoint
            ValueType.WINDSPEED -> return data.WindSpeed
            ValueType.WINDDIRECTION -> return data.WindDirection
            ValueType.GEOPOTENTIAL-> return  data.Geopotential
            else -> return data.Temperature
        }
    }

    private fun getArgument(data:InputData, argumentType: ArgumentType ) :Double {
        when (argumentType) {
            ArgumentType.TIME -> return data.Time /60.0
            ArgumentType.PRESSURE -> return data.Pressure
            ArgumentType.GEOPOTENTIAL -> return data.Geopotential
            ArgumentType.DEWPOINT -> return  data.DewPoint
            ArgumentType.TEMPERATURE -> return  data.Temperature
            ArgumentType.HUMIDITY -> return  data.Humidity
            ArgumentType.WINDSPEED -> return data.WindSpeed
            ArgumentType.WINDDIRECTION -> return data.WindDirection
            else -> return data.Time/60.0
        }
    }


    
    /*override fun getArgument(position: Int): Double {
        return dataList.get(position).x
    }

    override fun getValue(p0: Int): Double {
        return dataList.get(p0).y
    }

    override fun getDataCount(): Int {
        return dataList.size
    }*/

}


data class SeriesItem (var argument:Double, var value:Double) {}


enum class ArgumentType {
    TIME,PRESSURE,GEOPOTENTIAL,ALTITUDE,TEMPERATURE,HUMIDITY,WINDSPEED,WINDDIRECTION,DEWPOINT
}

enum class ValueType {
    PRESSURE,TEMPERATURE,HUMIDITY,WINDSPEED,WINDDIRECTION,DEWPOINT,ALTITUDE,GEOPOTENTIAL
}
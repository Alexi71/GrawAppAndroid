package de.graw.android.grawapp.controller.chart

import android.provider.BaseColumns
import android.transition.ChangeTransform
import android.util.Log
import com.devexpress.dxcharts.NumericSeriesData
import com.syncfusion.charts.ChartDataPoint
import de.graw.android.grawapp.model.InputData
import org.jetbrains.anko.collections.forEachByIndex

class InputSeriesData(val data: ArrayList<InputData>,val argumentType:ArgumentType,val valueType:ValueType):NumericSeriesData {

    private var inputData:ArrayList<InputData>
    var dataList  = ArrayList<ChartDataPoint<Double,Double>>()

    init {
        this.inputData = data
    }

     fun initialize() {
        this.inputData.forEach {
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
            else -> return data.Time
        }
    }


    
    override fun getArgument(position: Int): Double {
        return dataList.get(position).x
    }

    override fun getValue(p0: Int): Double {
        return dataList.get(p0).y
    }

    override fun getDataCount(): Int {
        return dataList.size
    }

}


data class SeriesItem (var argument:Double, var value:Double) {}


enum class ArgumentType {
    TIME,PRESSURE,GEOPOTENTIAL,ALTITUDE
}

enum class ValueType {
    PRESSURE,TEMPERATURE,HUMIDITY,WINDSPEED,WINDDIRECTION,DEWPOINT,ALTITUDE,GEOPOTENTIAL
}
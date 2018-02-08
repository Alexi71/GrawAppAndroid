package de.graw.android.grawapp.Fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.syncfusion.charts.*
import com.syncfusion.charts.enums.Visibility
import de.graw.android.grawapp.R
import de.graw.android.grawapp.controller.chart.ArgumentType
import de.graw.android.grawapp.controller.chart.InputSeriesData
import de.graw.android.grawapp.controller.chart.ValueType
import de.graw.android.grawapp.model.InputData
import de.graw.android.grawapp.model.MessageEvent
import de.graw.android.grawapp.model.MessageEventType
import org.greenrobot.eventbus.EventBus


/**
 * A simple [Fragment] subclass.
 * Use the [ChartTimeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChartTimeFragment : ChartBaseFragment() {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments.getString(ARG_PARAM1)
            mParam2 = arguments.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_chart_time, container, false)
        chart = view.findViewById<SfChart>(R.id.time_chart)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var inputData = arguments.getSerializable("inputdata") as ArrayList<InputData>
        if(inputData != null) {
            Log.i("test","Hello from chart fragment ${inputData.size}")
            initializeChart(inputData)
            EventBus.getDefault().post(MessageEvent(MessageEventType.CHART_LOADER,"",null));
        }
    }

    override fun initializeChart(dataList:ArrayList<InputData>) {

        val xAxis = NumericalAxis()
        xAxis.interval = 10.0
        chart!!.primaryAxis = xAxis
        chart!!.legend.visibility = Visibility.Visible

        chart!!.setOnTooltipCreatedListener(object:SfChart.OnTooltipCreatedListener{
            override fun onCreated(sfChart: SfChart?, tooltip: TooltipView?) {
                val label = tooltip!!.series.label
                if(tooltip.chartDataPoint != null) {
                    val datapoint = tooltip.chartDataPoint as ChartDataPoint<Double,Double>
                    val xValue = datapoint.x
                    val yValue = datapoint.y
                    when(label) {
                        getString(R.string.chart_temperature) -> {
                            tooltip!!.label= "${xValue.format(1)} °min ${yValue.format(1)} °C"
                        }
                        getString(R.string.chart_pressure) -> {
                            tooltip!!.label= "${xValue.format(1)} °min ${yValue.format(1)} mB"
                        }
                        getString(R.string.chart_humidity) -> {
                            tooltip!!.label= "${xValue.format(1)} °min ${yValue.format(0)} %"
                        }
                        getString(R.string.chart_wind_speed) -> {
                            tooltip!!.label= "${xValue.format(1)} °min ${yValue.format(1)} m/s"
                        }
                    }
                }
            }
        })

        getLineSeries(dataList,getString(R.string.chart_temperature), ValueType.TEMPERATURE,ContextCompat.getColor(context, R.color.grawLightBlue),
        -90.0,50.0,false,true)

        getLineSeries(dataList,getString(R.string.chart_pressure) ,ValueType.PRESSURE,ContextCompat.getColor(context, R.color.grawRed),
                1.0,1150.0,true,false)

        getLineSeries(dataList,getString(R.string.chart_humidity) ,ValueType.HUMIDITY,ContextCompat.getColor(context, R.color.grawOrange),
        0.0,100.0,false,false)

        getLineSeries(dataList,getString(R.string.chart_wind_speed), ValueType.WINDSPEED,ContextCompat.getColor(context, R.color.grawMagenta),
                0.0,200.0,false,false)

        getScatterSeries(dataList,getString(R.string.chart_temperature), ValueType.TEMPERATURE,ContextCompat.getColor(context, R.color.grawLightBlue),
                -90.0,50.0,false,false)

        getScatterSeries(dataList,getString(R.string.chart_pressure) ,ValueType.PRESSURE,ContextCompat.getColor(context, R.color.grawRed),
                1.0,1150.0,true,false)

        getScatterSeries(dataList,getString(R.string.chart_humidity) ,ValueType.HUMIDITY,ContextCompat.getColor(context, R.color.grawOrange),
                0.0,100.0,false,false)

        getScatterSeries(dataList,getString(R.string.chart_wind_speed), ValueType.WINDSPEED,ContextCompat.getColor(context, R.color.grawMagenta),
                0.0,200.0,false,false)




    }



 /*   private fun getLineSeries(dataList:ArrayList<InputData>,text:String,valueType:ValueType,color:Int,
                          yAxixMin:Double,yAxisMax:Double,isLogarithmicAxis:Boolean,addSecondaryAxis:Boolean) {
        val series = FastLineSeries()
        series.color = color
        series.label = text
        if(isLogarithmicAxis) {
            val yAxis = LogarithmicAxis()
            yAxis.minimum = yAxixMin
            yAxis.maximum = yAxisMax
            yAxis.isInversed = false
            yAxis.visibility = Visibility.Gone
            series.yAxis = yAxis
            yAxis.showMajorGridLines = addSecondaryAxis
            if(addSecondaryAxis){
                chart!!.secondaryAxis = yAxis
            }
        }
        else {
            val yAxis = NumericalAxis()
            yAxis.minimum = yAxixMin
            yAxis.maximum = yAxisMax
            yAxis.isInversed = false
            yAxis.visibility = Visibility.Gone
            series.yAxis = yAxis
            yAxis.showMajorGridLines = addSecondaryAxis
            if(addSecondaryAxis){
                chart!!.secondaryAxis = yAxis
            }
        }

        var data = InputSeriesData(dataList,ArgumentType.TIME, valueType)
        data.initialize()
        series.dataSource = data.dataList
        chart!!.series.add(series)

    }

    private fun getScatterSeries(dataList:ArrayList<InputData>,text:String,valueType:ValueType,color:Int,
                              yAxixMin:Double,yAxisMax:Double,isLogarithmicAxis:Boolean,addSecondaryAxis:Boolean) {
        val series = ScatterSeries()
        series.color = color
        series.label = text
        series.visibilityOnLegend = Visibility.Gone


        /*series.dataMarker.isShowLabel = true
        series.dataMarker.labelStyle.textColor = color
        series.setOnDataMarkerLabelCreatedListener(object:ChartSeries.OnDataMarkerLabelCreatedListener{
            override fun onLabelCreated(label: DataMarkerLabel?) {

            }
        })*/
        if(isLogarithmicAxis) {
            val yAxis = LogarithmicAxis()
            yAxis.minimum = yAxixMin
            yAxis.maximum = yAxisMax
            yAxis.isInversed = false
            yAxis.visibility = Visibility.Gone
            series.yAxis = yAxis
            yAxis.showMajorGridLines = addSecondaryAxis
            if(addSecondaryAxis){
                chart!!.secondaryAxis = yAxis
            }
        }
        else {
            val yAxis = NumericalAxis()
            yAxis.minimum = yAxixMin
            yAxis.maximum = yAxisMax
            yAxis.isInversed = false
            yAxis.visibility = Visibility.Gone
            series.yAxis = yAxis
            yAxis.showMajorGridLines = addSecondaryAxis
            if(addSecondaryAxis){
                chart!!.secondaryAxis = yAxis
            }
        }

        var data = InputSeriesData(dataList,ArgumentType.TIME, valueType)
        data.initialize(true)
        series.dataSource = data.dataList
        chart!!.series.add(series)

    }*/

   /* fun initializeChart(dataList:ArrayList<InputData>) {
        var series = ScatterLineSeries()
        series.displayName = "Temperature"
        var result = InputSeriesData(dataList,ArgumentType.TIME, ValueType.TEMPERATURE)
        result.initialize()

        series.data = result
        chart!!.addSeries(series)

        val xAxis = NumericAxisX()

        xAxis.gridAlignment = 10.0
        //xAxis.gridOffset = 10.0
        chart!!.axisX = xAxis

        var yAxis = NumericAxisY()
        yAxis.gridAlignment = 5.0
        //chart!!.axisY = yAxis
        var axisLabel = AxisLabel()
        axisLabel.textFormat = "# °C"
        yAxis.label = axisLabel

        var pressureSeries = ScatterLineSeries()
        pressureSeries.displayName = "Pressure"
        result = InputSeriesData(dataList,ArgumentType.TIME, ValueType.PRESSURE)
        result.initialize()
        pressureSeries.data = result
        yAxis = NumericAxisY()
        yAxis.gridAlignment = 100.0
        chart!!.axisY = yAxis
        axisLabel = AxisLabel()
        axisLabel.textFormat = "# mB"
        yAxis.label = axisLabel
        chart!!.addSeries(pressureSeries)

        val legend = Legend()
        legend.setHorizontalPosition(LegendHorizontalPosition.CENTER)
        legend.setVerticalPosition(LegendVerticalPosition.TOP_OUTSIDE)
        legend.setOrientation(LegendOrientation.LEFT_TO_RIGHT)
        chart!!.legend = legend

    }*/

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChartTimeFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): ChartTimeFragment {
            val fragment = ChartTimeFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor

enum class CHART_TYPE {
    TEMPERATURE,PRESSURE,HUMIDITY,WINDSPEED,WINDDIRECTION,DEWPOINT,ALTITUDE,GEOPOTENTIAL
}

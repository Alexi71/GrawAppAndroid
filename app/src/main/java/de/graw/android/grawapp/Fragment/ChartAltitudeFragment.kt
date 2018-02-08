package de.graw.android.grawapp.Fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.syncfusion.charts.*
import com.syncfusion.charts.enums.Visibility

import de.graw.android.grawapp.R
import de.graw.android.grawapp.controller.chart.ArgumentType
import de.graw.android.grawapp.controller.chart.ValueType
import de.graw.android.grawapp.model.InputData
import de.graw.android.grawapp.model.MessageEvent
import de.graw.android.grawapp.model.MessageEventType
import org.greenrobot.eventbus.EventBus


/**
 * A simple [Fragment] subclass.
 * Use the [ChartAltitudeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChartAltitudeFragment : ChartBaseFragment() {

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

    override fun initializeChart(dataList: ArrayList<InputData>) {
        val yAxis = NumericalAxis()
        yAxis.interval = 2000.0
        yAxis.isInversed = false
        yAxis.title.text = "Altitude [km]"
        yAxis.setOnLabelCreatedListener(object: ChartAxis.OnLabelCreatedListener {
            override fun onLabelCreated(p0: Int, axisLabel: ChartAxis.ChartAxisLabel?) {
                //axisLabel!!.labelContent = "${axisLabel.labelContent} m"
                val newValue = axisLabel!!.position / 1000.0
                axisLabel!!.labelContent = "$newValue"
            }
        } )
        chart!!.secondaryAxis = yAxis
        chart!!.legend.visibility = Visibility.Visible

        chart!!.setOnTooltipCreatedListener(object:SfChart.OnTooltipCreatedListener {
            override fun onCreated(sfChart: SfChart?, tooltip: TooltipView?) {
                val label = tooltip!!.series.label
                if(tooltip.chartDataPoint != null) {
                    val datapoint = tooltip.chartDataPoint as ChartDataPoint<Double, Double>
                    val xValue = datapoint.x
                    val yValue = datapoint.y
                    when(label) {
                        getString(R.string.chart_temperature) -> {
                            tooltip!!.label =  "${xValue.format(1)} °C ${yValue.format(0)} m"
                        }
                        getString(R.string.chart_pressure) -> {
                            tooltip!!.label = "${xValue.format(1)} mB ${yValue.format(0)} m"
                        }
                        getString(R.string.chart_humidity) -> {
                            tooltip!!.label = "${xValue.format(1)} % ${yValue.format(0)} m"
                        }
                        getString(R.string.chart_wind_speed) -> {
                            tooltip!!.label = "${xValue.format(1)} m/s ${yValue.format(0)} m"
                        }
                    }
                }
            }
        })
        getLineSeriesAxisX(dataList,getString(R.string.chart_temperature), ArgumentType.TEMPERATURE, ValueType.ALTITUDE,
                ContextCompat.getColor(context, R.color.grawLightBlue),-90.0,40.0,true,
                Visibility.Gone)

        getLineSeriesAxisX(dataList,getString(R.string.chart_pressure), ArgumentType.PRESSURE, ValueType.ALTITUDE,
                ContextCompat.getColor(context, R.color.grawRed),1.0,1100.0,false,
                Visibility.Gone,true)

        getLineSeriesAxisX(dataList,getString(R.string.chart_humidity), ArgumentType.HUMIDITY, ValueType.ALTITUDE,
                ContextCompat.getColor(context, R.color.grawOrange),0.0,100.0,false)

        getLineSeriesAxisX(dataList,getString(R.string.chart_wind_speed), ArgumentType.WINDSPEED, ValueType.ALTITUDE,
                ContextCompat.getColor(context, R.color.grawMagenta),0.0,120.0,false)


    }



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
         * @return A new instance of fragment ChartAltitudeFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): ChartAltitudeFragment {
            val fragment = ChartAltitudeFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor

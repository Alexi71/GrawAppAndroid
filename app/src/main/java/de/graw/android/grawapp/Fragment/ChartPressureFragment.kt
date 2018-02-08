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
import de.graw.android.grawapp.controller.chart.ValueType
import de.graw.android.grawapp.model.InputData
import de.graw.android.grawapp.model.MessageEvent
import de.graw.android.grawapp.model.MessageEventType
import org.greenrobot.eventbus.EventBus
import java.text.DecimalFormat


/**
 * A simple [Fragment] subclass.
 * Use the [ChartPressureFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChartPressureFragment : ChartBaseFragment() {

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
        val view = inflater!!.inflate(R.layout.fragment_chart_pressure, container, false)
        chart = view.findViewById<SfChart>(R.id.pressure_chart)
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
        val yAxis = LogarithmicAxis()

        yAxis.isInversed = true

        /*yAxis.setOnLabelCreatedListener(object:ChartAxis.OnLabelCreatedListener {
            override fun onLabelCreated(p0: Int, axisLabel: ChartAxis.ChartAxisLabel?) {
                axisLabel!!.labelContent = "${axisLabel.labelContent} mb"

            }
        } )*/
        yAxis.title.text = getString(R.string.chart_pressure_y_title)
        chart!!.secondaryAxis = yAxis
        chart!!.legend.visibility = Visibility.Visible

        chart!!.setOnTooltipCreatedListener(object:SfChart.OnTooltipCreatedListener {
            override fun onCreated(sfChart: SfChart?, tooltip: TooltipView?) {
                val label = tooltip!!.series.label
                if(tooltip.chartDataPoint != null) {
                    val datapoint = tooltip.chartDataPoint as ChartDataPoint<Double,Double>
                    val xValue = datapoint.x
                    val yValue = datapoint.y
                    when(label) {
                        getString(R.string.chart_temperature),getString(R.string.chart_dewpoint) -> {
                            val text = "${xValue.format(1)} °C ${yValue.format(1)} mB"
                            tooltip!!.label = text
                            Log.i("test",text)
                        }
                    }
                }
            }
        })
        getLineSeriesAxisX(dataList,getString(R.string.chart_temperature),ArgumentType.TEMPERATURE,ValueType.PRESSURE,
                ContextCompat.getColor(context, R.color.grawLightBlue),-90.0,40.0,true,
                Visibility.Visible)

        getLineSeriesAxisX(dataList,getString(R.string.chart_dewpoint),ArgumentType.DEWPOINT,ValueType.PRESSURE,
                ContextCompat.getColor(context, R.color.grawOrange),-90.0,40.0,false )


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
         * @return A new instance of fragment ChartPressureFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): ChartPressureFragment {
            val fragment = ChartPressureFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor

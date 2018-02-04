package de.graw.android.grawapp


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.syncfusion.charts.LineSeries
import com.syncfusion.charts.NumericalAxis
import com.syncfusion.charts.SfChart
import de.graw.android.grawapp.controller.chart.ArgumentType
import de.graw.android.grawapp.controller.chart.InputSeriesData
import de.graw.android.grawapp.controller.chart.ValueType
import de.graw.android.grawapp.model.InputData


/**
 * A simple [Fragment] subclass.
 * Use the [ChartTimeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChartTimeFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    var chart:SfChart? = null

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
        }
    }

    fun initializeChart(dataList:ArrayList<InputData>) {

        val xAxis = NumericalAxis()
        xAxis.interval = 10.0
        val yAxis = NumericalAxis()
        yAxis.minimum = -90.0
        yAxis.maximum = 50.0
        chart!!.primaryAxis = xAxis
        chart!!.secondaryAxis = yAxis
        val series = LineSeries()
        var data = InputSeriesData(dataList,ArgumentType.TIME, ValueType.TEMPERATURE)
        data.initialize()
        series.dataSource = data.dataList

        chart!!.series.add(series)
    }

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

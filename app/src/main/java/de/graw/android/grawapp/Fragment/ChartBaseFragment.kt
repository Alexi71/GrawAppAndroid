package de.graw.android.grawapp.Fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.syncfusion.charts.*
import com.syncfusion.charts.enums.Visibility

import de.graw.android.grawapp.R
import de.graw.android.grawapp.controller.chart.ArgumentType
import de.graw.android.grawapp.controller.chart.InputSeriesData
import de.graw.android.grawapp.controller.chart.ValueType
import de.graw.android.grawapp.model.InputData
import java.text.DecimalFormat


/**
 * A simple [Fragment] subclass.
 * Use the [ChartBaseFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
open class ChartBaseFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    protected var chart: SfChart? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments.getString(ARG_PARAM1)
            mParam2 = arguments.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val textView = TextView(activity)
        textView.setText(R.string.hello_blank_fragment)
        return textView
    }

    protected open fun initializeChart(dataList:ArrayList<InputData>) {}

    protected open fun getLineSeriesAxisX(dataList: ArrayList<InputData>, text: String, argumentType: ArgumentType,
                                          valueType: ValueType, color: Int, xAxixMin: Double, xAxisMax:Double,
                                          addPrimaryAxis:Boolean, isAxisVisibility: Visibility = Visibility.Gone,
                                          isLogarithmicAxis: Boolean = false, isInverted:Boolean=false) {
        val series = FastLineSeries()
        series.color = color
        series.label = text
        series.isTooltipEnabled = true
        series.isAnimationEnabled = true
        if(!isLogarithmicAxis) {
            val xAxis = NumericalAxis()
            xAxis.minimum = xAxixMin
            xAxis.maximum = xAxisMax
            xAxis.isInversed = isInverted
            xAxis.visibility = isAxisVisibility
            series.xAxis = xAxis
            xAxis.showMajorGridLines = addPrimaryAxis
            if (addPrimaryAxis) {
                chart!!.primaryAxis = xAxis
            }
        }
        else{
            val xAxis = LogarithmicAxis()
            xAxis.isInversed = isInverted
            xAxis.visibility = isAxisVisibility
            series.xAxis = xAxis
            xAxis.showMajorGridLines = addPrimaryAxis
            if (addPrimaryAxis) {
                chart!!.primaryAxis = xAxis
            }
        }


        var data = InputSeriesData(dataList, argumentType, valueType)
        data.initialize()
        series.dataSource = data.dataList
        chart!!.series.add(series)
    }


    protected open fun getLineSeries(dataList:ArrayList<InputData>, text:String, valueType: ValueType, color:Int,
                              yAxixMin:Double, yAxisMax:Double, isLogarithmicAxis:Boolean, addSecondaryAxis:Boolean) {
        val series = FastLineSeries()
        series.color = color
        series.label = text
        series.isTooltipEnabled = true
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

        var data = InputSeriesData(dataList, ArgumentType.TIME, valueType)
        data.initialize()
        series.dataSource = data.dataList
        chart!!.series.add(series)

    }

    protected open fun getScatterSeries(dataList:ArrayList<InputData>, text:String, valueType: ValueType, color:Int,
                                 yAxixMin:Double, yAxisMax:Double, isLogarithmicAxis:Boolean, addSecondaryAxis:Boolean) {
        val series = ScatterSeries()
        series.color = color
        series.label = text
        series.visibilityOnLegend = Visibility.Gone
        series.scatterHeight = 10.0f
        series.scatterWidth = 10.0f
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

        var data = InputSeriesData(dataList, ArgumentType.TIME, valueType)
        data.initialize(true)
        series.dataSource = data.dataList
        chart!!.series.add(series)

    }

    protected fun Double.format(fracDigits: Int): String {
        val df = DecimalFormat()
        df.setMaximumFractionDigits(fracDigits)
        return df.format(this)
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
         * @return A new instance of fragment ChartBaseFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): ChartBaseFragment {
            val fragment = ChartBaseFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor

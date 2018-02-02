package de.graw.android.grawapp.Fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.syncfusion.charts.ChartDataPoint
import com.syncfusion.charts.LineSeries
import com.syncfusion.charts.NumericalAxis
import com.syncfusion.charts.SfChart

import de.graw.android.grawapp.R


/**
 * A simple [Fragment] subclass.
 * Use the [ChartDemoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChartDemoFragment : Fragment() {

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
        val view = inflater!!.inflate(R.layout.fragment_chart_demo, container, false)
        chart = view.findViewById<SfChart>(R.id.sfChart)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val xAxis = NumericalAxis()
        xAxis.interval = 1.0
        val yAxis = NumericalAxis()
        yAxis.minimum = -90.0
        yAxis.maximum = 50.0
        chart!!.primaryAxis = xAxis
        chart!!.secondaryAxis = yAxis
        val series = LineSeries()

        val data = ArrayList<ChartDataPoint<Double,Double>>()
        data.add(ChartDataPoint(0.0,10.0))
        data.add(ChartDataPoint(2.0,5.0))
        data.add(ChartDataPoint(3.0,1.0))
        data.add(ChartDataPoint(4.0,-5.0))
        data.add(ChartDataPoint(5.0,-10.0))
        series.dataSource = data

        chart!!.series.add(series)

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
         * @return A new instance of fragment ChartDemoFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): ChartDemoFragment {
            val fragment = ChartDemoFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor

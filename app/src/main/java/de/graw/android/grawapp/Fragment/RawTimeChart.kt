package de.graw.android.grawapp.Fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.syncfusion.charts.ChartDataPoint
import com.syncfusion.charts.NumericalAxis
import com.syncfusion.charts.SfChart
import com.syncfusion.charts.TooltipView
import com.syncfusion.charts.enums.Visibility
import de.graw.android.grawapp.R
import de.graw.android.grawapp.controller.Firebase.FireBaseInputControllerFromUrlListener
import de.graw.android.grawapp.controller.Firebase.FirebaseHelper
import de.graw.android.grawapp.controller.Firebase.FirebaseSnapshotListener
import de.graw.android.grawapp.controller.InputDataController
import de.graw.android.grawapp.controller.chart.ValueType
import de.graw.android.grawapp.model.InputData
import de.graw.android.grawapp.model.MessageEvent
import de.graw.android.grawapp.model.MessageEventType
import de.graw.android.grawapp.model.RawData
import org.greenrobot.eventbus.EventBus

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class RawTimeChart : ChartBaseFragment() {

    private var dataListener: FirebaseSnapshotListener? = null
    private var inputDataListener: FireBaseInputControllerFromUrlListener? = null

    var firebaseHelper: FirebaseHelper? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_chart_time, container, false)
        chart = view.findViewById<SfChart>(R.id.time_chart)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val inputData: ArrayList<InputData> = ArrayList<InputData>()
        inputData.add(0,InputData())
        initializeChart(inputData)

        dataListener  = object:FirebaseSnapshotListener {
            override fun onSetData(data: RawData) {
                Log.i("test","data time ${data.epochTime}")
                Log.i("test","url ${data.url}")
                if(data.url.isNotEmpty())
                    firebaseHelper?.loadData(data.url)
            }


        }

        inputDataListener = object:FireBaseInputControllerFromUrlListener {
            override fun onSetInputController(inputDataController: InputDataController) {
                chart?.series?.clear()
                val data = inputDataController.dataList!!.toCollection(ArrayList())
                initializeChart(data)
            }
        }
        firebaseHelper = arguments.getSerializable("firebase") as FirebaseHelper
        firebaseHelper?.addListener(dataListener!!)
        firebaseHelper?.addDataListener(inputDataListener!!)

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
                    val datapoint = tooltip.chartDataPoint as ChartDataPoint<Double, Double>
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

        getLineSeries(dataList,getString(R.string.chart_temperature), ValueType.TEMPERATURE, ContextCompat.getColor(context, R.color.grawLightBlue),
                -90.0,50.0,false,true)

        getLineSeries(dataList,getString(R.string.chart_pressure) , ValueType.PRESSURE, ContextCompat.getColor(context, R.color.grawRed),
                1.0,1150.0,true,false)

        getLineSeries(dataList,getString(R.string.chart_humidity) , ValueType.HUMIDITY, ContextCompat.getColor(context, R.color.grawOrange),
                0.0,100.0,false,false)

        getLineSeries(dataList,getString(R.string.chart_wind_speed), ValueType.WINDSPEED, ContextCompat.getColor(context, R.color.grawMagenta),
                0.0,200.0,false,false)

        getScatterSeries(dataList,getString(R.string.chart_temperature), ValueType.TEMPERATURE, ContextCompat.getColor(context, R.color.grawLightBlue),
                -90.0,50.0,false,false)

        getScatterSeries(dataList,getString(R.string.chart_pressure) , ValueType.PRESSURE, ContextCompat.getColor(context, R.color.grawRed),
                1.0,1150.0,true,false)

        getScatterSeries(dataList,getString(R.string.chart_humidity) , ValueType.HUMIDITY, ContextCompat.getColor(context, R.color.grawOrange),
                0.0,100.0,false,false)

        getScatterSeries(dataList,getString(R.string.chart_wind_speed), ValueType.WINDSPEED, ContextCompat.getColor(context, R.color.grawMagenta),
                0.0,200.0,false,false)




    }

    override fun onResume() {
        super.onResume()
        if(userVisibleHint) {
            Log.i("test","view is visible")
            if(firebaseHelper != null && dataListener != null) {
                firebaseHelper!!.addListener(dataListener!!)
                firebaseHelper!!.addDataListener(inputDataListener!!)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        firebaseHelper!!.removeListener(dataListener!!)
        firebaseHelper!!.removeDataListener(inputDataListener!!)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if(!isVisibleToUser) {
            if(firebaseHelper != null && dataListener != null) {
                firebaseHelper!!.removeListener(dataListener!!)
                firebaseHelper!!.removeDataListener(inputDataListener!!)
            }
        }
        else {
            if(firebaseHelper != null && dataListener != null) {
                firebaseHelper!!.addListener(dataListener!!)
                firebaseHelper!!.addDataListener(inputDataListener!!)
            }
        }
    }


}

package de.graw.android.grawapp.Fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import de.graw.android.grawapp.R
import de.graw.android.grawapp.controller.Firebase.FirebaseHelper
import de.graw.android.grawapp.controller.Firebase.FirebaseSnapshotListener
import de.graw.android.grawapp.model.RawData
import java.text.DecimalFormat


/**
 * A simple [Fragment] subclass.
 * Use the [RawValueFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RawValueFragment() : Fragment() {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    private var mView:View? = null
    private var pressureView:View? = null
    private var temperatureView:View? = null
    private var humidityView:View? = null
    private var windspeedView:View? = null

    private var textViewPressueValue:TextView? = null
    private var textViewPressureHeader:TextView? = null
    private var pressureImage:ImageView? = null

    private var textViewTemperatureValue:TextView? = null
    private var textViewTemperatureHeader:TextView? = null
    private var temperatureImage:ImageView? = null

    private var textViewHumdityValue:TextView? = null
    private var textViewHumidityHeader:TextView? = null
    private var humidityImage:ImageView? = null
    private var textViewWindspeedValue:TextView? = null
    private var textViewWindspeedHeader:TextView? = null
    private var windspeedImage:ImageView? = null

    private var dataListener:FirebaseSnapshotListener? = null

    var firebaseHelper:FirebaseHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments.getString(ARG_PARAM1)
            mParam2 = arguments.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mView = inflater!!.inflate(R.layout.fragment_raw_value, container, false)
        return mView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pressureView = mView!!.findViewById(R.id.PressureRawValue)
        temperatureView = mView!!.findViewById(R.id.TemperatureRawValue)
        humidityView = mView!!.findViewById(R.id.HumidityRawValue)
        windspeedView = mView!!.findViewById(R.id.WindspeedRawValue)

        textViewPressueValue = pressureView!!.findViewById(R.id.textViewValue)
        textViewPressureHeader= pressureView!!.findViewById(R.id.textViewHeader)
        pressureImage = pressureView!!.findViewById(R.id.themeIcon)
        pressureImage!!.setImageResource(R.drawable.icons8_pressure_filled_50)
        textViewPressureHeader!!.setText("Pressure")

        textViewTemperatureValue = temperatureView!!.findViewById(R.id.textViewValue)
        textViewTemperatureHeader= temperatureView!!.findViewById(R.id.textViewHeader)
        temperatureImage = temperatureView!!.findViewById(R.id.themeIcon)
        temperatureImage!!.setImageResource(R.drawable.icons8_temperature_48)
        textViewTemperatureHeader!!.setText("Temperature")

        textViewHumdityValue = humidityView!!.findViewById(R.id.textViewValue)
        textViewHumidityHeader= humidityView!!.findViewById(R.id.textViewHeader)
        humidityImage = humidityView!!.findViewById(R.id.themeIcon)
        humidityImage!!.setImageResource(R.drawable.icons8_water_50)
        textViewHumidityHeader!!.setText("Humidity")

        textViewWindspeedValue = windspeedView!!.findViewById(R.id.textViewValue)
        textViewWindspeedHeader= windspeedView!!.findViewById(R.id.textViewHeader)
        windspeedImage = windspeedView!!.findViewById(R.id.themeIcon)
        windspeedImage!!.setImageResource(R.drawable.icons8_fan_filled_50)
        textViewWindspeedHeader!!.setText("Wind speed")

        dataListener  = object:FirebaseSnapshotListener {
            override fun onSetData(data: RawData) {
                Log.i("test","data time ${data.epochTime}")
                val formatter = DecimalFormat("#")
                textViewPressueValue!!.setText("%.1f".format(data.pressure))
                textViewTemperatureValue!!.setText("%.1f".format(data.temperature))
                textViewHumdityValue!!.setText(formatter.format(data.humdity))
                textViewWindspeedValue!!.setText("%.1f".format(data.windspeed))

            }

        }

        firebaseHelper = arguments.getSerializable("firebase") as FirebaseHelper



       // firebaseHelper!!.addListener(dataListener!!)

    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        if(userVisibleHint) {
            Log.i("test","view is visible")
            if(firebaseHelper != null && dataListener != null) {
                firebaseHelper!!.addListener(dataListener!!)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        firebaseHelper!!.removeListener(dataListener!!)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if(!isVisibleToUser) {
            if(firebaseHelper != null && dataListener != null) {
                firebaseHelper!!.removeListener(dataListener!!)
            }
        }
        else {
            if(firebaseHelper != null && dataListener != null) {
                firebaseHelper!!.addListener(dataListener!!)
            }
        }
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
         * @return A new instance of fragment RawValueFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): RawValueFragment {
            val fragment = RawValueFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor

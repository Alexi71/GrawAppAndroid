package de.graw.android.grawapp.Fragment


import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import de.graw.android.grawapp.Fragment.adapter.PageRawSwipeAdpater

import de.graw.android.grawapp.R
import de.graw.android.grawapp.controller.Firebase.FirebaseHelper
import de.graw.android.grawapp.controller.Firebase.FirebaseSnapshotListener
import de.graw.android.grawapp.model.RawData
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import android.view.KeyEvent.KEYCODE_BACK






/**
 * A simple [Fragment] subclass.
 * Use the [RawContainerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RawContainerFragment : Fragment() {


    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    var mView:View? = null
    var pageView:ViewPager? = null
    //var tabLayout:TabLayout? = null
    private var statusView:View? = null
    private var textViewStartTime:TextView? = null
    private var textViewLastTimeUpdate:TextView? = null
    private var gpsImage:ImageView? = null
    private var sensorImage:ImageView? = null
    private var telemetryImage:ImageView? = null
    private var stationKey:String? = null
    private var flightKey:String? = null
    private var fireBaseHelper:FirebaseHelper? = null
    private var startDetected:Boolean = false
    private var dataListener:FirebaseSnapshotListener? = null
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
        mView = inflater!!.inflate(R.layout.fragment_raw_container, container, false)
        return mView
    }

    override  fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pageView = mView?.findViewById(R.id.RawPageView)
        //tabLayout = mView?.findViewById(R.id.RawTab)


        statusView = mView?.findViewById(R.id.status_header)
        //tabLayout?.setupWithViewPager(pageView)
        textViewStartTime = statusView?.findViewById(R.id.raw_start_time)
        textViewLastTimeUpdate = statusView?.findViewById(R.id.raw_last_time_update)
        gpsImage = statusView?.findViewById(R.id.raw_image_gps)
        sensorImage = statusView?.findViewById(R.id.raw_image_sensor)
        telemetryImage = statusView?.findViewById(R.id.raw_image_telemetry)
        gpsImage?.setImageResource(R.drawable.g5_status_32x32_grau)
        sensorImage?.setImageResource(R.drawable.g5_status_32x32_grau)
        telemetryImage?.setImageResource(R.drawable.g5_status_32x32_grau)

        stationKey = arguments.getString("stationkey")
        flightKey = arguments.getString("flightkey")

        dataListener = object :FirebaseSnapshotListener{
            override fun onSetData(data: RawData) {
                setData(data)
            }
        }
        if(stationKey != null && flightKey != null) {
           fireBaseHelper = FirebaseHelper(dataListener)
            fireBaseHelper?.startRawDataListener(stationKey!!,flightKey!!)
        }
        val adpater = PageRawSwipeAdpater(fragmentManager,fireBaseHelper!!)
        pageView?.adapter = adpater
    }

     fun setData(data: RawData) {
         Log.i("test","Raw container gets data")
        val dateTime = DateTime (data.epochTime.toLong() * 1000L)
        val dateDecoder = DateTimeFormat.forPattern("HH:mm:ss")
        textViewLastTimeUpdate?.setText("Last update: ${dateTime.toString(dateDecoder)}")
        sensorImage?.setImageResource(setImage(data.sensorStatus))
        telemetryImage?.setImageResource(setImage(data.telemetryStatus))
        gpsImage?.setImageResource(setImage(data.gpsStatus))
        if(data.startDetected && !startDetected) {
            val date = DateTime(data.startTime.toLong()*1000L)
            textViewStartTime?.setText("Start time: ${date.toString(dateDecoder)}")
            startDetected = true
        }
        else if (!data.startDetected && !startDetected) {
            textViewStartTime?.setText("Start time: ---")
        }

    }

    private fun setImage(value:Long):Int {

        when (value) {
            0L-> return R.drawable.g5_status_32x32_gruen
            1L-> return R.drawable.g5_status_32x32_rot
            2L->return R.drawable.g5_status_32x32_grau
            else -> return R.drawable.g5_status_32x32_grau
        }
    }

    override  fun onResume() {
        super.onResume()
        if(view == null) {
            return
        }

        view!!.isFocusableInTouchMode = true
        view!!.requestFocus()

        view!!.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                Log.i("test","${event.action}  $keyCode")
                if (event.action === KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    Log.i("test","action here!")
                    fireBaseHelper?.removeListener(dataListener!!)
                    fireBaseHelper?.stopRawDataListener()
                }
               return false
            }
        })
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
         * @return A new instance of fragment RawContainerFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): RawContainerFragment {
            val fragment = RawContainerFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor

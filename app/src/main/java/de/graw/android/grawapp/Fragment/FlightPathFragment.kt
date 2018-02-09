package de.graw.android.grawapp.Fragment


import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*

import de.graw.android.grawapp.R
import de.graw.android.grawapp.model.InputData


/**
 * A simple [Fragment] subclass.
 * Use the [FlightPathFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FlightPathFragment : Fragment(), OnMapReadyCallback {



    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    private var map:GoogleMap? = null
    private var mView:View? =null
    var mMapView:MapView? = null
    var inputData = ArrayList<InputData>()
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
        mView = inflater!!.inflate(R.layout.fragment_flight_path, container, false)
        return mView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity.title = "Flight Path"
        inputData = arguments.getSerializable("inputdata") as ArrayList<InputData>

        mMapView = mView!!.findViewById<MapView>(R.id.map_flight_path)
        if(mMapView !=null) {
            mMapView?.onCreate(null)
            mMapView?.onResume()
            mMapView?.getMapAsync(this)
        }
    }
    override fun onMapReady(p0: GoogleMap?) {
        MapsInitializer.initialize(context)
        map = p0
        map!!.mapType= GoogleMap.MAP_TYPE_NORMAL

        val options = PolylineOptions()
        options.color(Color.RED)
        options.width(5f)
        inputData.forEach {
            options.add(LatLng(it.Latitude,it.Longitude))
        }

        map!!.addPolyline(options)
        map!!.addMarker(MarkerOptions().position(LatLng(inputData.first().Latitude,inputData.first().Longitude))
                .title("Start Point"))
        map!!.addMarker(MarkerOptions().position(LatLng(inputData.last().Latitude,inputData.last().Longitude))
                .title("End Point"))
                //.snippet("I hope I can get tehre some day"))
        var bounds:LatLngBounds? = null
        if(inputData.first().Latitude < inputData.last().Latitude) {
             bounds = LatLngBounds(LatLng(inputData.first().Latitude, inputData.first().Longitude),
                    LatLng(inputData.last().Latitude, inputData.last().Longitude))
        }else {
            bounds = LatLngBounds(LatLng(inputData.last().Latitude, inputData.last().Longitude),
                    LatLng(inputData.first().Latitude, inputData.first().Longitude))
        }
        //val cameraPosition = CameraPosition.builder().target(LatLng(lat,lon)).zoom(16f).bearing(0f).tilt(45f).build()
        map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(bounds.center,10f))
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
         * @return A new instance of fragment FlightPathFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): FlightPathFragment {
            val fragment = FlightPathFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }


    }

}// Required empty public constructor

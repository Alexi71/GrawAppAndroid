package de.graw.android.grawapp.Fragment


import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*

import de.graw.android.grawapp.R
import de.graw.android.grawapp.controller.Firebase.FireBaseInputControllerFromUrlListener
import de.graw.android.grawapp.controller.Firebase.FirebaseHelper
import de.graw.android.grawapp.controller.Firebase.FirebaseSnapshotListener
import de.graw.android.grawapp.controller.InputDataController
import de.graw.android.grawapp.model.InputData
import de.graw.android.grawapp.model.RawData
import java.text.DecimalFormat


/**
 * A simple [Fragment] subclass.
 * Use the [RawMapFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RawMapFragment : Fragment(), OnMapReadyCallback {


    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    private var mView:View? = null
    private var mMap:GoogleMap? = null
    private var mMapView: MapView? = null

    private var dataListener: FirebaseSnapshotListener? = null

    var firebaseHelper: FirebaseHelper? = null
    private var inputDataListener: FireBaseInputControllerFromUrlListener? = null
    var polyLine:Polyline? = null
    var startMarker:Marker? = null
    var endMarker:Marker? = null
    var zoomValue = 10f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments.getString(ARG_PARAM1)
            mParam2 = arguments.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mView = inflater!!.inflate(R.layout.fragment_flight_path, container, false)
        return mView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mMapView = mView!!.findViewById<MapView>(R.id.map_flight_path)
        if(mMapView !=null) {
            mMapView?.onCreate(null)
            mMapView?.onResume()
            mMapView?.getMapAsync(this)

        }

        dataListener  = object:FirebaseSnapshotListener {
            override fun onSetData(data: RawData) {
                Log.i("test","data time ${data.epochTime}")
                /*mMap?.addMarker(MarkerOptions().position(LatLng(data.latitude,data.longitude))
                        .title("Start Point"))
                val cameraPosition = CameraPosition.builder().target(LatLng(data.latitude,data.longitude)).zoom(16f).bearing(0f).tilt(45f).build()
                mMap?.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))*/
                if(data.url.isNotEmpty()) {
                    firebaseHelper?.loadData(data.url)
                } else {
                    mMap?.addMarker(MarkerOptions().position(LatLng(data.latitude, data.longitude))
                            .title("Start Point"))
                    val cameraPosition = CameraPosition.builder().target(LatLng(data.latitude, data.longitude)).zoom(16f).bearing(0f).tilt(45f).build()
                    mMap?.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                }
            }
        }

        inputDataListener = object:FireBaseInputControllerFromUrlListener {
            override fun onSetInputController(inputDataController: InputDataController) {

                val data = inputDataController.dataList!!.toCollection(ArrayList())
                drawPolyline(data)
            }
        }

        firebaseHelper = arguments.getSerializable("firebase") as FirebaseHelper
        firebaseHelper?.addListener(dataListener!!)
        firebaseHelper?.addDataListener(inputDataListener!!)

    }

    fun drawPolyline(inputData:List<InputData>)
    {
        polyLine?.remove()
        startMarker?.remove()
        endMarker?.remove()
        val options = PolylineOptions()
        options.color(Color.RED)
        options.width(5f)
        inputData.forEach {
            options.add(LatLng(it.Latitude,it.Longitude))
        }

        polyLine = mMap!!.addPolyline(options)
        startMarker = mMap!!.addMarker(MarkerOptions().position(LatLng(inputData.first().Latitude,inputData.first().Longitude))
                .title("Start Point"))
        endMarker = mMap!!.addMarker(MarkerOptions().position(LatLng(inputData.last().Latitude,inputData.last().Longitude))
                .title("End Point"))
        //.snippet("I hope I can get tehre some day"))
        var bounds: LatLngBounds? = null
        if(inputData.first().Latitude < inputData.last().Latitude) {
            bounds = LatLngBounds(LatLng(inputData.first().Latitude, inputData.first().Longitude),
                    LatLng(inputData.last().Latitude, inputData.last().Longitude))
        }else {
            bounds = LatLngBounds(LatLng(inputData.last().Latitude, inputData.first().Longitude),
                    LatLng(inputData.first().Latitude, inputData.last().Longitude))
        }
        Log.i("test","center: ${bounds.center}")
        //val cameraPosition = CameraPosition.builder().target(LatLng(lat,lon)).zoom(16f).bearing(0f).tilt(45f).build()
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(bounds.center,zoomValue))
    }

    override fun onMapReady(p0: GoogleMap?) {
        MapsInitializer.initialize(context)
        mMap = p0
        mMap!!.mapType= GoogleMap.MAP_TYPE_NORMAL
        mMap!!.uiSettings.isScrollGesturesEnabled = false
        mMap!!.setOnCameraMoveListener {
            val camera = mMap!!.cameraPosition
            Log.i("test","$camera.zoom")
            zoomValue = camera.zoom
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
         * @return A new instance of fragment RawMapFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): RawMapFragment {
            val fragment = RawMapFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor

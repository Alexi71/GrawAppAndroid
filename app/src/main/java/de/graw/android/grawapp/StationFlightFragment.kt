package de.graw.android.grawapp


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


/**
 * A simple [Fragment] subclass.
 * Use the [StationFlightFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StationFlightFragment : Fragment(),OnMapReadyCallback {


    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    var mView:View? = null
    var mMap:GoogleMap? = null
    var mMapView:MapView? = null
    var station :StationItem? = null
    var flightListView:ListView? = null

    var flightList = ArrayList<FlightData>()
    var adapter:FlightAdapter? = null

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
        mView = inflater!!.inflate(R.layout.fragment_station_flight, container, false)
        flightListView = mView!!.findViewById<ListView>(R.id.flightList)
        return mView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mMapView = mView!!.findViewById<MapView>(R.id.map)
        if(mMapView !=null) {
            mMapView?.onCreate(null)
            mMapView?.onResume()
            mMapView?.getMapAsync(this)
        }
        adapter = FlightAdapter(context,flightList)
        flightListView?.adapter = adapter

        station = arguments.getSerializable("stationItem") as StationItem
        if(station != null) {
            Log.i("test","Hello from station flight overview ${station!!.city}")
            getData(station!!)
        }
        else {
            Log.i("test", "item was null from activity.....")
        }
    }

    fun getData(station:StationItem) {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference().child("station").child(station.key).child("flights")

        val listener = object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                if(data!!.exists()) {
                    //val hasChildren = data.hasChildren()
                    //val map = data.getValue() as Map<String,Any>

                    val children = data.children
                    flightList.clear()
                    children.forEach {
                        var item = FlightData()
                        Log.i("flight test",it.key)
                        val map = it.getValue() as Map<String, Any>
                        item.key = it.key
                        item.time = map.get("Time") as String
                        item.date = map.get("Date") as String
                        item.fileName =   map.get("FileName") as String

                        if(map.containsKey("IsRealTimeDataAvailable")) {
                            item.isRealTimeData = map.get("IsRealTimeDataAvailable") as Boolean
                        }
                        if(map.containsKey("Url")) {
                            item.url = map.get("Url") as String
                        }
                        if(map.containsKey("UrlEnd")) {
                            item.urlEnd = map.get("UrlEnd") as String
                        }
                        if(map.containsKey("Url100")) {
                            item.url100 = map.get("Url100") as String
                        }
                        flightList.add(item)
                        adapter?.notifyDataSetChanged()
                       /* station.key = it.key
                        station.city = map.get("City") as String
                        station.country = map.get("Country") as String
                        station.latitude = map.get("Latitude") as String
                        station.longitude = map.get("Longitude") as String
                        station.altitude = map.get("Altitude") as String
                        station.name = map.get("Name") as String
                        stationList.add(station)
                        adapter?.notifyDataSetChanged()*/
                        //Log.i("test","map")
                    }

                    /* for(i in data.children) {
                         val key = i.child("key").value as String
                         Log.i("test","key: ${key}")

                        /* val item = i.getValue<StationItem>(StationItem::class.java)
                         Log.i("test","received from db ${item!!.city}")*/
 12
                     }*/
                }
            }

        }
        ref.addValueEventListener(listener)
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
         * @return A new instance of fragment StationFlightFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): StationFlightFragment {
            val fragment = StationFlightFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onMapReady(p0: GoogleMap?) {
        MapsInitializer.initialize(context)
        mMap = p0
        mMap!!.mapType= GoogleMap.MAP_TYPE_NORMAL
        var lat = 40.689247
        var lon = -74.044502
        if(station != null)  {
            lat = station!!.latitude.toDouble()
            lon = station!!.longitude.toDouble()
        }
        mMap!!.addMarker(MarkerOptions().position(LatLng(lat,lon))
                .title("Statue of Liberty")
                .snippet("I hope I can get tehre some day"))
        val cameraPosition = CameraPosition.builder().target(LatLng(lat,lon)).zoom(16f).bearing(0f).tilt(45f).build()
        mMap!!.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

    }

}// Required empty public constructor

class FlightAdapter(context: Context, list:ArrayList<FlightData>) :
        BaseAdapter(){

    private var list:ArrayList<FlightData>
    private var context:Context

    init {
        this.list = list
        this.context = context
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val item = list[position]
        val layoutInflator = LayoutInflater.from(context)
        val inflator = layoutInflator.inflate(R.layout.flight_data_layout,parent,false)
        val data = inflator.findViewById<TextView>(R.id.textViewData)
        val time = inflator.findViewById<TextView>(R.id.textViewTime)
        data.text = item.date.toString()
        time.text = item.time.toString()
        return inflator
    }

    override fun getItem(position: Int): Any {
       return list[position]
    }

    override fun getItemId(position: Int): Long {
      return position.toLong()
    }

    override fun getCount(): Int {
       return list.size
    }


}
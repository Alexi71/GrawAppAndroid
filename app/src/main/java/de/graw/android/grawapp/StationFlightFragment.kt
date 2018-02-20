package de.graw.android.grawapp



import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment

//import android.app.Fragment
import android.support.v4.content.ContextCompat.getColor
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.borax12.materialdaterangepicker.date.DatePickerDialog
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import de.graw.android.grawapp.callback.SwipeToDeleteCallback
import de.graw.android.grawapp.dataBase.TableHelper
import de.graw.android.grawapp.helper.Settings.Settings
import de.graw.android.grawapp.model.FlightData
import de.graw.android.grawapp.model.StationItem
import de.graw.android.grawapp.model.interfaces.OnItemClickListener
import org.jetbrains.anko.alert
import org.joda.time.DateTime
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [StationFlightFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StationFlightFragment : Fragment(),OnMapReadyCallback,OnItemClickListener,
        DatePickerDialog.OnDateSetListener{



    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    var mView:View? = null
    var mMap:GoogleMap? = null
    var mMapView:MapView? = null
    var station : StationItem? = null
    var recycleListView:RecyclerView? = null
    var dateImage:ImageView? = null
    var dateTextView:TextView? = null

    //var flightListView:ListView? = null

    var flightList = ArrayList<FlightData>()
    var adapter:FlightRecyclerAdapter? = null


    val flighFragment = FlightOverviewFragment()

    var firstDay:DateTime? = null
    var lastDay:DateTime? = null


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
        //flightListView = mView!!.findViewById<ListView>(R.id.flightList)
        dateImage = mView!!.findViewById(R.id.dateRangeImage)
        dateTextView = mView!!.findViewById(R.id.dateRangeText)
        recycleListView = mView!!.findViewById<RecyclerView>(R.id.recyclerView)
        recycleListView!!.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        recycleListView!!.layoutManager = LinearLayoutManager(context)
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
        flightList.clear()
        val settings = Settings(context)

        //val formatter = DateTimeFormat.forPattern("dd/MM/yyyy")
        firstDay = settings.getFirstDay()//.parseDateTime(DateItem.getFirstDayOfWeek())
        lastDay = settings.getLastDay()//.parseDateTime(DateItem.getLastDayOfWeek())

        setDateTextView(firstDay!!.year,firstDay!!.monthOfYear-1,firstDay!!.dayOfMonth,
                lastDay!!.year,lastDay!!.monthOfYear-1,lastDay!!.dayOfMonth)


        adapter = FlightRecyclerAdapter(flightList,this)
        //flightListView?.adapter = adapter
        recycleListView!!.adapter = adapter
        val swipeHandler = object : SwipeToDeleteCallback(context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = recycleListView!!.adapter as FlightRecyclerAdapter
                Log.i("test","$viewHolder.adapterPosition")
                val data = flightList[viewHolder.adapterPosition]
                context.alert("Do you want delete flight from ${data.date}?","Confirm delete") {
                    positiveButton(getString(R.string.ok_value)) {
                        removeData(data)
                        //adapter.removeItem(viewHolder.adapterPosition)
                    }
                    negativeButton(getString(R.string.cancel_value)) {
                        //reload data dismiss animation.....
                        adapter.notifyDataSetChanged()
                    }
                }.show()

            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recycleListView)



        station = arguments.getSerializable("stationItem") as StationItem
        if(station != null) {
            Log.i("test","Hello from station flight overview ${station!!.city}")
            getData(station!!)
            //getRawData(station!!)
        }
        else {
            Log.i("test", "item was null from activity.....")
        }
        dateImage?.setOnClickListener {
            val now = Calendar.getInstance()
            //now.set(2018,5,21)
            Log.i("test","month: ${now.get(Calendar.MONTH)}")
           /* val dpd = com.borax12.materialdaterangepicker.date.DatePickerDialog.newInstance(
                null,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)*/
       // )

            val dpd =DatePickerDialog.newInstance(this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH))
            dpd.accentColor = getColor(context, R.color.grawOrange)

            dpd.isAutoHighlight = true
            //dpd.setOnDateSetListener(this)
            dpd.show(activity.fragmentManager,"Hello")
        }
        activity.title = "${station!!.name}, ${station!!.id}"
        /*flightListView!!.setOnItemClickListener { adapterView, view, position, id ->
            val item = flightList[position]
            val bundle = Bundle()
            bundle.putSerializable("flight",item)
            flighFragment.arguments = bundle
            val transAction = fragmentManager.beginTransaction()
            transAction.replace(R.id.contentArea,flighFragment)
                    .addToBackStack(null)
                    .commit()
            //setTitle("Flight Data")
        }*/
    }
    override fun onItemClick(item: FlightData) {
        Log.i("test","${item.url}")
        if(item.isRealTimeData) {
            return
        }
        val bundle = Bundle()
        bundle.putSerializable("flight",item)
        flighFragment.arguments = bundle
        val transAction = fragmentManager.beginTransaction()
        transAction.replace(R.id.contentArea,flighFragment)
                .addToBackStack(null)
                .commit()
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int,
                           dayOfMonth: Int, yearEnd: Int, monthOfYearEnd: Int, dayOfMonthEnd: Int) {
       setDateTextView(year,monthOfYear,dayOfMonth,yearEnd,monthOfYearEnd,dayOfMonthEnd)
       adapter!!.clearItems()
       getData(station!!)
    }

    private fun setDateTextView(year: Int, monthOfYear: Int,
                                dayOfMonth: Int, yearEnd: Int, monthOfYearEnd: Int, dayOfMonthEnd: Int){

        val date = "$dayOfMonth/${monthOfYear+1}/$year - $dayOfMonthEnd/${monthOfYearEnd+1}/$yearEnd"
        Log.i("test",date)
        dateTextView?.setText(date)
        firstDay = DateTime(year,monthOfYear+1,dayOfMonth,0,0,0,0)
        lastDay = DateTime(yearEnd,monthOfYearEnd+1,dayOfMonthEnd,0,0,0,0)
        var settings = Settings(context)
        settings.saveFlightDates(firstDay!!,lastDay!!)
    }

    fun removeData(item:FlightData) {
        try {
            val db = TableHelper(context)
            db.deleteFlightData(item.key)
        }catch (e:Exception){
            Log.i("test","${e.localizedMessage}")
        }

        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.getReferenceFromUrl(item.url)
        storageRef.delete().addOnSuccessListener {
            Log.i("test","file deleted ${item.url}")
        }.addOnFailureListener {
                    Log.i("test",it.localizedMessage)
                }

        if(!item.url100.isNullOrEmpty()) {
            val storageRef = storage.getReferenceFromUrl(item.url100)
            storageRef.delete().addOnSuccessListener {
                Log.i("test","file deleted ${item.url100}")
            }.addOnFailureListener {
                        Log.i("test",it.localizedMessage)
                    }
        }

        if(!item.urlEnd.isNullOrEmpty()) {
            val storageRef = storage.getReferenceFromUrl(item.urlEnd)
            storageRef.delete().addOnSuccessListener {
                Log.i("test","file deleted ${item.urlEnd}")
            }.addOnFailureListener {
                        Log.i("test",it.localizedMessage)
                    }
        }

        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference().child("station").child(station!!.key).child("flights").child(item.key)
        ref.removeValue()
    }

    fun getData(station: StationItem) {

        val firstDayEpoch = firstDay!!.millis/ 1000.0
        val lastDayEpoch = lastDay!!.millis/ 1000.0
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference().child("station").child(station.key).child("flights")
               .orderByChild("EpochTime").startAt(firstDayEpoch).endAt(lastDayEpoch)


        val listener = object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                if(data!!.exists()) {
                    //val hasChildren = data.hasChildren()
                    //val map = data.getValue() as Map<String,Any>

                    val children = data.children
                    adapter!!.clearItems()
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
                        if(map.containsKey("EpochTime")) {
                            item.epochTime = map.get("EpochTime") as Double
                            val dateTime = DateTime (item.epochTime.toLong() * 1000L)
                            Log.i("test",dateTime.toString())
                            val epoch = dateTime.millis /1000
                            Log.i("test","from db: ${item.epochTime} from calc: ${epoch}")
                        }
                       // flightList.add(item)
                        //if(!item.isRealTimeData) {
                            adapter?.addItem(item)
                        //}
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

        val childListener = object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                Log.i("test","snapshot error")
            }

            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
                Log.i("test","snapshot moved")
            }

            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
                Log.i("test","snapshot changed")
            }

            override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                Log.i("test","snapshot added")
            }

            override fun onChildRemoved(p0: DataSnapshot?) {
                Log.i("test","snapshot removed")
            }

        }
        ref.addChildEventListener(childListener)
    }

    fun getRawData(station: StationItem) {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference().child("station").child(station.key).child("flights")

        val listener = object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                if(data!!.exists()) {
                    val children = data.children
                    //adapter!!.clearItems()
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
                        if(map.containsKey("EpochTime")) {
                            item.epochTime = map.get("EpochTime") as Double
                            val dateTime = DateTime (item.epochTime.toLong() * 1000L)
                            Log.i("test",dateTime.toString())
                            val epoch = dateTime.millis /1000
                            Log.i("test","from db: ${item.epochTime} from calc: ${epoch}")
                        }
                        // flightList.add(item)
                        if(item.isRealTimeData) {
                            adapter?.insertItemAtFirst(item)
                        }

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
                .title("${station!!.name} staion")
                .snippet("station id: ${station!!.id}"))
        val cameraPosition = CameraPosition.builder().target(LatLng(lat,lon)).zoom(16f).bearing(0f).tilt(45f).build()
        mMap!!.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

    }

}// Required empty public constructor

class FlightRecyclerAdapter(val items:ArrayList<FlightData>, listener:OnItemClickListener):RecyclerView.Adapter<HolderAnswer>() {

    private val onItemClickListener:OnItemClickListener

    init {
        this.onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderAnswer {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.flight_data_layout,parent,false)
        return HolderAnswer(view,onItemClickListener)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: HolderAnswer, position: Int) {
        holder.connect(items.get(position))
    }

    fun addItem(item:FlightData) {
        items.add(item)
        notifyItemInserted(items.size)
    }

    fun insertItemAtFirst(item:FlightData) {
        items.add(0,item)
        notifyItemInserted(items.size)
    }
    fun removeItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }
    fun clearItems() {
        items.clear()
        notifyDataSetChanged()
    }
}

class HolderAnswer(itemView:View,listener:OnItemClickListener):RecyclerView.ViewHolder(itemView) {
    private val onItemClickListener:OnItemClickListener
    init {
        this.onItemClickListener = listener
    }

    fun connect(item:FlightData) {
        val data = itemView.findViewById<TextView>(R.id.textViewData)
        val time = itemView.findViewById<TextView>(R.id.textViewTime)
        data.text = item.date.toString()
        time.text = item.time.toString()
        if(item.isRealTimeData) {
            data.setTextColor(getColor(itemView.context, R.color.grawOrange))
            time.setTextColor(getColor(itemView.context, R.color.grawOrange))
        }
        itemView.setOnClickListener {
            Log.i("test","content clicked ${item.key}")
            onItemClickListener.onItemClick(item)
        }
    }
}

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
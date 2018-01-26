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
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.greenrobot.eventbus.EventBus


/**
 * A simple [Fragment] subclass.
 * Use the [StationListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StationListFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    private var stationList = ArrayList<StationItem>()
    var adapter : StationAdapter? = null
    var listView:ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments.getString(ARG_PARAM1)
            mParam2 = arguments.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_station_list, container, false)
        listView = view.findViewById<ListView>(R.id.listStation)

        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       /* stationList.add(StationItem("1","Graw","Nueremberg","Germany","","","",""))
        stationList.add(StationItem("2","NOAA","Sterling","USA","","","",""))
        stationList.add(StationItem("2","NCA","Toronto","Canada","","","",""))*/
        adapter = StationAdapter(context,stationList)
        listView!!.adapter = adapter

        getData()


        listView!!.setOnItemClickListener { adapterView, view, position, id ->
            //Toast.makeText(context,"Position clicked ${position}",Toast.LENGTH_LONG).show()
            val item = stationList[position]
            EventBus.getDefault().post(item)
        }

    }

    fun getData() {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("/station")
        val listener = object :ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                if(data!!.exists()) {
                    val children = data.children
                    stationList.clear()
                    children.forEach {
                        var station = StationItem()
                        Log.i("test",it.key)
                        val map = it.getValue() as Map<String, Any>
                        station.key = it.key
                        station.city = map.get("City") as String
                        station.country = map.get("Country") as String
                        station.latitude = map.get("Latitude") as String
                        station.longitude = map.get("Longitude") as String
                        station.altitude = map.get("Altitude") as String
                        station.name = map.get("Name") as String
                        stationList.add(station)
                        adapter?.notifyDataSetChanged()
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
         * @return A new instance of fragment StationListFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): StationListFragment {
            val fragment = StationListFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor

class StationAdapter(context:Context, stationList:ArrayList<StationItem>):BaseAdapter() {

    private var stationList:ArrayList<StationItem>
    private var context:Context

    init {
        this.stationList = stationList
        this.context= context
    }

    override fun getView(index: Int, view: View?, parent: ViewGroup?): View {
        val item = stationList[index]
        val layoutInflator = LayoutInflater.from(context)
        val inflator = layoutInflator.inflate(R.layout.station_information_row,parent,false)
        val city = inflator.findViewById<TextView>(R.id.textViewCity)
        val country = inflator.findViewById<TextView>(R.id.textViewCountry)
        val name = inflator.findViewById<TextView>(R.id.textViewName)
        city.text = item.city
        country.text = item.country
        name.text = item.name

        return inflator
    }

    override fun getItem(index: Int): Any {
       return stationList[index]
    }

    override fun getItemId(index: Int): Long {
       return index.toLong()
    }

    override fun getCount(): Int {
       return stationList.size
    }

}

package de.graw.android.grawapp


import android.app.ProgressDialog
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.firebase.storage.FirebaseStorage
import de.graw.android.grawapp.Fragment.ChartSwipeFragment
import de.graw.android.grawapp.Fragment.FlightPathFragment
import de.graw.android.grawapp.Fragment.MessageSwipeFragment
import de.graw.android.grawapp.controller.InputDataController
import de.graw.android.grawapp.dataBase.TableHelper
import de.graw.android.grawapp.model.FlightData
import de.graw.android.grawapp.model.MessageEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.doAsync
import org.json.JSONArray
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Serializable
import org.jetbrains.anko.*


/**
 * A simple [Fragment] subclass.
 * Use the [FlightOverviewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FlightOverviewFragment : Fragment() {



    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    var chartFragment = ChartSwipeFragment()
    var flightPathFragment = FlightPathFragment()
    var flightData: FlightData? = null
    var inputDataController:InputDataController? = null
    var frameLayout:FrameLayout? = null
    var dialog:ProgressDialog? = null

    var bottomNavigation:BottomNavigationView? =null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments.getString(ARG_PARAM1)
            mParam2 = arguments.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        EventBus.getDefault().register(this)
        var view = inflater!!.inflate(R.layout.fragment_flight_overview, container, false)

        frameLayout = view.findViewById(R.id.pageViewArea)
        bottomNavigation = view.findViewById(R.id.navigation)

        bottomNavigation!!.setOnNavigationItemSelectedListener (object:BottomNavigationView.OnNavigationItemSelectedListener{
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                Log.i("test","${item.title}")
                when(item.itemId){
                    R.id.action_chart -> {
                       loadChartFragment()
                    }
                    R.id.action_path -> {
                        loadPathFragment()
                    }
                    R.id.action_message -> {
                        loadMessageFragment()
                    }
                }
                return true
            }
        })




        // Inflate the layout for this fragment
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog = context.indeterminateProgressDialog ("Hello")
        flightData = arguments.getSerializable("flight") as FlightData
        if(flightData != null) {
            //check database first if the flight data in local db.....

            Log.i("test","Hello from station flight overview ${flightData!!.url}")
            /*val busyIndicator = SfBusyIndicator(context)
            busyIndicator.viewBoxHeight = 100
            busyIndicator.viewBoxWidth = 100
            busyIndicator.textColor = Color.RED
            busyIndicator.animationType = AnimationTypes.SingleCircle
            frameLayout!!.addView(busyIndicator)*/
            dialog!!.show()
            if(!loadFromDb(flightData!!.key)) {
                loadData()
            }

        }
        else {
            Log.i("test", "item was null from activity.....")
        }
    }

    fun loadFromDb(flightKey:String):Boolean {
        val db = TableHelper(context)
        val dataList = db.getFlightData(flightKey)
        if(dataList.size > 0) {
            inputDataController = InputDataController()
            inputDataController!!.dataList = dataList
            loadChartFragment()
            Log.i("test","load from db was sucessfully")
            return true
        }
        return false
    }

    fun loadData()  {

        val storage = FirebaseStorage.getInstance()
        val ref = storage.getReferenceFromUrl(flightData!!.url)
        val localFile: File = File.createTempFile("data", "json")
        ref.getFile(localFile)
                .addOnSuccessListener { taskSnapshot ->
                    // Local temp file has been created
                    // use localFile
                    var inputStream = localFile.inputStream()
                    inputDataController = InputDataController()
                    inputDataController!!.getDataFromJson(inputStream)

                    if(inputDataController!!.dataList != null) {
                        doAsync {
                            val db = TableHelper(context)
                             db.insertFlightData(inputDataController!!.dataList!!, flightData!!.key)
                        }
                        loadChartFragment()
                    }

                   /* val gson = Gson()
                    val jsonStream = InputStreamReader(inputStream)

// Create a buffered reader
                    val bufferedReader = BufferedReader(jsonStream)
                    val result:List<InputData> = gson.fromJson(bufferedReader,Array<InputData>::class.java).toList()

                    Log.i("test","${result.size}")*/


                   /*async(UI) {
                        var result = bg{convertStreamToString(inputStream)}
                        parseJson( result.await())
                    }*/



                    //var json = JSONObject(localFile)

                }
                .addOnFailureListener { exception ->
                    // Handle any errors
                }
                .addOnProgressListener { taskSnapshot ->
                    // taskSnapshot.bytesTransferred
                    // taskSnapshot.totalByteCount
                    Log.i("test","bytes transfered ${taskSnapshot.bytesTransferred}")
                }
    }

    private fun loadChartFragment() {
        frameLayout!!.removeAllViews()
        val bundle = Bundle()
        bundle.putSerializable("inputdata", inputDataController!!.dataList as Serializable)
        chartFragment = ChartSwipeFragment()
        chartFragment.arguments = bundle
        val transAction = fragmentManager.beginTransaction()
        transAction.replace(R.id.pageViewArea, chartFragment)
                //.addToBackStack(null)
                .commit()

    }

    private fun loadPathFragment() {
        frameLayout!!.removeAllViews()
        val bundle = Bundle()
        bundle.putSerializable("inputdata", inputDataController!!.dataList as Serializable)
        flightPathFragment = FlightPathFragment()
        flightPathFragment.arguments = bundle
        val transAction = fragmentManager.beginTransaction()
        transAction.replace(R.id.pageViewArea, flightPathFragment)
                //.addToBackStack(null)
                .commit()

    }

    private fun loadMessageFragment() {
        frameLayout!!.removeAllViews()
        val bundle = Bundle()
        bundle.putSerializable("flightData", flightData )
        val frm = MessageSwipeFragment()
        frm.arguments = bundle
        val transAction = fragmentManager.beginTransaction()
        transAction.replace(R.id.pageViewArea, frm)
                //.addToBackStack(null)
                .commit()

    }

    @Subscribe
    fun onChartLoaded(messageEvent: MessageEvent) {
        Log.i("test","chart loaded")
        dialog!!.dismiss()
    }


    fun parseJson(text:String) {
        Log.i("test", text)
        var jsonObject = JSONArray(text)
        Log.i("test","got json")
    }

    private fun convertStreamToString(inputStream: InputStream): String {
        val reader = BufferedReader(InputStreamReader(inputStream))
        var line:String
        var result:String = ""

        try {
            do {
                line = reader.readLine()
                if(line != null) {
                    result +=line
                }
            }while (line != null)
        }catch(ex:Exception) {}
        return result
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
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
         * @return A new instance of fragment FlightOverviewFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): FlightOverviewFragment {
            val fragment = FlightOverviewFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor

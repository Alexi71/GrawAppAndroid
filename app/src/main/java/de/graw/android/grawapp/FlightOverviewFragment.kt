package de.graw.android.grawapp


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.storage.FirebaseStorage
import de.graw.android.grawapp.Fragment.ChartDemoFragment
import de.graw.android.grawapp.controller.InputDataController
import de.graw.android.grawapp.model.FlightData
import org.json.JSONArray
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Serializable


/**
 * A simple [Fragment] subclass.
 * Use the [FlightOverviewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FlightOverviewFragment : Fragment() {



    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    var chartFragment = ChartTimeFragment()
    var flightData: FlightData? = null
    var inputDataController:InputDataController? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments.getString(ARG_PARAM1)
            mParam2 = arguments.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var view = inflater!!.inflate(R.layout.fragment_flight_overview, container, false)


        // Inflate the layout for this fragment
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        flightData = arguments.getSerializable("flight") as FlightData
        if(flightData != null) {
            Log.i("test","Hello from station flight overview ${flightData!!.url}")
            loadData()
        }
        else {
            Log.i("test", "item was null from activity.....")
        }



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
                        val bundle = Bundle()
                        bundle.putSerializable("inputdata",inputDataController!!.dataList as Serializable)
                        chartFragment = ChartTimeFragment()
                        chartFragment.arguments = bundle
                        val transAction = fragmentManager.beginTransaction()
                        transAction.replace(R.id.pageViewArea,chartFragment)
                                //.addToBackStack(null)
                                .commit()
                    }

                   /* val gson = Gson()
                    val jsonStream = InputStreamReader(inputStream)

// Create a buffered reader
                    val bufferedReader = BufferedReader(jsonStream)
                    val result:List<InputData> = gson.fromJson(bufferedReader,Array<InputData>::class.java).toList()

                    Log.i("test","${result.size}")*/


                 /*   async(UI) {
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

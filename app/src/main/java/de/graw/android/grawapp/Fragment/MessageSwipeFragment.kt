package de.graw.android.grawapp.Fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.graw.android.grawapp.Fragment.adapter.PageChartSwipeAdapter
import de.graw.android.grawapp.Fragment.adapter.PageMessageSwipeAdapter

import de.graw.android.grawapp.R
import de.graw.android.grawapp.model.FlightData
import de.graw.android.grawapp.model.InputData


/**
 * A simple [Fragment] subclass.
 * Use the [MessageSwipeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MessageSwipeFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    private var pageView: ViewPager? = null
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
        val view = inflater!!.inflate(R.layout.fragment_chart_swipe, container, false)
        pageView = view.findViewById(R.id.pageView)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity.title = "Messages"
        val flightData = arguments.getSerializable("flightData") as FlightData
        val adapter = PageMessageSwipeAdapter(fragmentManager,flightData)
        pageView!!.adapter = adapter
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
         * @return A new instance of fragment MessageSwipeFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): MessageSwipeFragment {
            val fragment = MessageSwipeFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor

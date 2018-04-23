package de.graw.android.grawapp.Fragment.adapter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import de.graw.android.grawapp.Fragment.*
import de.graw.android.grawapp.controller.Firebase.FirebaseHelper
import de.graw.android.grawapp.model.FlightData

/**
 * Created by KotikA on 21.02.2018.
 */
class PageRawSwipeAdpater(fm: FragmentManager?,firebaseHelper: FirebaseHelper) : FragmentStatePagerAdapter(fm) {

    val firebaseHelper:FirebaseHelper

    init {
        this.firebaseHelper = firebaseHelper
    }

    override fun getItem(position: Int): Fragment {
        when(position) {
            0 -> {
                val fragment = RawValueFragment()
                val bundle = Bundle()
                bundle.putSerializable("firebase",firebaseHelper)
                //bundle.putSerializable("listener",firebaseHelper!)
                fragment.arguments = bundle
                return fragment
            }
            1-> {
                val fragment = RawMapFragment()
                val bundle = Bundle()
                bundle.putSerializable("firebase",firebaseHelper)
                fragment.arguments = bundle
                return fragment
            }
            2-> {
                val fragment = RawTimeChart()
                val bundle = Bundle()
                bundle.putSerializable("firebase",firebaseHelper)
                fragment.arguments = bundle
                return fragment
            }
            else -> {
                val fragment = RawValueFragment()
                val bundle = Bundle()
                bundle.putSerializable("firebase",firebaseHelper)
                fragment.arguments = bundle
                return fragment
            }

        }
    }

    override fun getCount(): Int {
        return 3
    }

   /* override fun getPageTitle(position: Int): CharSequence {
        when (position) {
            0-> {return "Raw Values"}
            else-> {return "Details"}
        }
    }*/

}
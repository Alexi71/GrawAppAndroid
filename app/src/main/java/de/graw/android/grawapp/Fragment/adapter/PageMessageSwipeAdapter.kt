package de.graw.android.grawapp.Fragment.adapter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import de.graw.android.grawapp.Fragment.ChartTimeFragment
import de.graw.android.grawapp.Fragment.MessageContentFragment
import de.graw.android.grawapp.model.FlightData
import org.jetbrains.anko.custom.async
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL


class PageMessageSwipeAdapter(fm: FragmentManager?, flightData:FlightData) : FragmentStatePagerAdapter(fm) {
    private var flightData:FlightData
    private var fm:FragmentManager

    init {
        this.flightData = flightData
        this.fm  = fm!!
    }

    override fun getItem(position: Int): Fragment {
        when(position) {
            0-> {
                val fragment = MessageContentFragment()
                val bundle = Bundle()
                bundle.putString("message",flightData.url100)
                fragment.arguments = bundle
                return fragment
            }
            1-> {
                val fragment = MessageContentFragment()
                val bundle = Bundle()
                bundle.putString("message",flightData.urlEnd)
                fragment.arguments = bundle
                return fragment
            }
            else -> {
                val fragment = MessageContentFragment()
                val bundle = Bundle()
                bundle.putString("message",flightData.url100)
                fragment.arguments = bundle
                return fragment
            }
        }
    }



    override fun getCount(): Int {
        return 2
    }
}

package de.graw.android.grawapp.Fragment.adapter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import de.graw.android.grawapp.Fragment.ChartAltitudeFragment
import de.graw.android.grawapp.Fragment.ChartTimeFragment
import de.graw.android.grawapp.Fragment.ChartDemoFragment
import de.graw.android.grawapp.Fragment.ChartPressureFragment
import de.graw.android.grawapp.model.InputData

class PageChartSwipeAdapter(fm: FragmentManager?,dataList:ArrayList<InputData>) : FragmentStatePagerAdapter(fm) {

    val dataList:ArrayList<InputData>

    init {
        this.dataList = dataList
    }
    override fun getItem(position: Int): Fragment {
        when(position){
            0->{

                val fragment = ChartTimeFragment()
                val bundle = Bundle()
                bundle.putSerializable("inputdata",dataList)
                fragment.arguments = bundle
                return fragment
            }
            1-> {
                val fragment = ChartPressureFragment()
                val bundle = Bundle()
                bundle.putSerializable("inputdata",dataList)
                fragment.arguments = bundle
                return fragment
            }
            2-> {
                val fragment = ChartAltitudeFragment()
                val bundle = Bundle()
                bundle.putSerializable("inputdata",dataList)
                fragment.arguments = bundle
                return fragment
            }
            else -> {
                val fragment = ChartDemoFragment()
                val bundle = Bundle()
                bundle.putInt("count",position+1)
                fragment.arguments = bundle
                return fragment
            }
        }

    }

    override fun getCount(): Int {
        return 3
    }
}

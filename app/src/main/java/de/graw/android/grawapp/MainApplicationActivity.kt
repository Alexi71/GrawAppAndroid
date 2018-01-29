package de.graw.android.grawapp

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import de.graw.android.grawapp.dataBase.TableHelper
import kotlinx.android.synthetic.main.activity_main_application.*
import kotlinx.android.synthetic.main.app_bar_main_application.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class MainApplicationActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val fragmentManager = supportFragmentManager
    val stationListFragment = StationListFragment()
    val stationFlightFragment = StationFlightFragment()
    var userItem:UserItem? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_application)
        setSupportActionBar(toolbar)



        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        val email = intent.getStringExtra("email")
        val userId = intent.getStringExtra("userid")
        userItem = UserItem(0,email,userId)

        //get default station....
        val helper = TableHelper(this)
        val defaultStation = helper.getDefaultStation(userItem!!)

        if(defaultStation != null ) {
            openFlightFragment(defaultStation)
        }
        else {
            val transAction = fragmentManager.beginTransaction()
            transAction.replace(R.id.contentArea, stationListFragment)
                    .addToBackStack(null)
                    .commit()
            setTitle("Select Station")
        }
    }

    @Subscribe
    fun onRowClicked(item:StationItem) {
        Log.i("test","row clicked ${item.name}")

        try {
            val helper = TableHelper(this)
            helper.setDefaultStation(userItem!!,item)
        }
        catch (e:Exception) {
            Log.i("test","could not set default station")
        }

        openFlightFragment(item)

        //Toast.makeText(context,"Position clicked ${position}",Toast.LENGTH_LONG).show()
    }

    private fun openFlightFragment(item:StationItem) {
        val bundle = Bundle()
        bundle.putSerializable("stationItem",item)

        stationFlightFragment!!.arguments = bundle
        val transAction = fragmentManager.beginTransaction()
        transAction.replace(R.id.contentArea,stationFlightFragment)
                .addToBackStack(null)
                .commit()
        setTitle("Flight Data")
    }


    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }



    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }



    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_application, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}

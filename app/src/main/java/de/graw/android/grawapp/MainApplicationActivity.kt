package de.graw.android.grawapp

import android.os.Bundle
import android.os.PersistableBundle
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
import de.graw.android.grawapp.model.StationItem
import de.graw.android.grawapp.model.UserItem


class MainApplicationActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val fragmentManager = supportFragmentManager
    var stationListFragment = StationListFragment()
    var stationFlightFragment = StationFlightFragment()
    var userItem: UserItem? = null
    var navigationView:NavigationView? = null

    val TAG_STATION = "stationFragment"
    val TAG_FLIGHTS = "flightFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_application)
        setSupportActionBar(toolbar)
        navigationView = findViewById(R.id.nav_view)



        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        val email = intent.getStringExtra("email")
        val userId = intent.getStringExtra("userid")
        userItem = UserItem(0, email, userId)

        //get default station....
        val helper = TableHelper(this)
        val defaultStation = helper.getDefaultStation(userItem!!)
        addMenuItems()

        if(savedInstanceState == null) {
            if (defaultStation != null) {
                openFlightFragment(defaultStation)
            } else {
                val transAction = fragmentManager.beginTransaction()
                transAction.replace(R.id.contentArea, stationListFragment,TAG_STATION)
                        .addToBackStack(null)
                        .commit()
                setTitle("Select Station")
            }
        }
        else {
            if (defaultStation != null) {
                stationFlightFragment = supportFragmentManager.findFragmentByTag(TAG_FLIGHTS) as StationFlightFragment
            }
            else {
                stationListFragment = supportFragmentManager.findFragmentByTag(TAG_STATION) as StationListFragment
            }
        }

    }

    override  fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        //getFragmentManager().putFragment(outState,"myFragment",stationFlightFragment)
    }

    fun addMenuItems () {
        var menu = navigationView!!.menu
        var menuGroup = menu.addSubMenu("My Stations")

        var helper = TableHelper(this)
        var stationList = helper.getUserSubscribedStation(userItem!!)

        stationList.forEach{
            var item = menuGroup.add(0,it.database_id,1,it.name).setIcon(R.drawable.ic_place_black_24dp)
            item.setOnMenuItemClickListener(MenuItem.OnMenuItemClickListener {menuItem ->
                onMenuItemClick(menuItem)
                true
            })


        }

      /*  var item = menuGroup.add(0,1,1,"Test1").setIcon(R.drawable.ic_place_black_24dp)
        item.setOnMenuItemClickListener(MenuItem.OnMenuItemClickListener {menuItem ->
            onMenuItemClick(menuItem)
            true
        })

        menuGroup.add(1,2,1,"Test2").setOnMenuItemClickListener(MenuItem.OnMenuItemClickListener {menuItem ->
            onMenuItemClick(menuItem)
            true
        })*/
    }

    fun onMenuItemClick(item: MenuItem): Boolean {
        Log.i("test","${item.itemId}")
        drawer_layout.closeDrawer(GravityCompat.START)
        val db = TableHelper(this)

        val station = db.getStation(item.itemId)
        if(station != null) {
            openFlightFragment(station)
        }

        return true
    }

    @Subscribe
    fun onRowClicked(item: StationItem) {
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

    private fun openFlightFragment(item: StationItem) {
        val bundle = Bundle()
        bundle.putSerializable("stationItem",item)
        stationFlightFragment = StationFlightFragment()
        stationFlightFragment!!.arguments = bundle
        val transAction = fragmentManager.beginTransaction()
        transAction.replace(R.id.contentArea,stationFlightFragment,TAG_FLIGHTS)
                //.addToBackStack(null)
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
        //val count = getFragmentManager().backStackEntryCount

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
            R.id.nav_add_station -> {
                val transAction = fragmentManager.beginTransaction()
                transAction.replace(R.id.contentArea, stationListFragment,TAG_STATION)
                        .addToBackStack(null)
                        .commit()
                setTitle("Select Station")
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

   /* fun getData() {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("/station")
        val listener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                if(data!!.exists()) {
                    val children = data.children
                    stationList.clear()
                    val tableHelper = TableHelper(context)

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
                        try {
                            tableHelper.saveStation(station)
                        }
                        catch (e:Exception) {
                            Log.i("test",e.localizedMessage)
                        }
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
    }*/
}

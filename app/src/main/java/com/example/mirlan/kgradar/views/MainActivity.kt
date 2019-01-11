package com.example.mirlan.kgradar.views

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import com.example.mirlan.kgradar.R
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.model.Marker
import android.Manifest
import android.arch.lifecycle.*
import android.arch.lifecycle.Observer
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.text.Html
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.example.mirlan.kgradar.data.model.PoliceLocation
import com.example.mirlan.kgradar.viewModel.MapViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.VisibleRegion
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions


const val REQUEST_CODE_LOCATION = 123
class MainActivity : FragmentActivity(), OnMapReadyCallback,GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener,GoogleMap.OnMarkerClickListener,LocationSource.OnLocationChangedListener {

    private var mAuth:FirebaseAuth?=null
    private lateinit var mMap: GoogleMap
    private var mDatabase: DatabaseReference? = null
    private var status:Int = 0

    private val viewModel: MapViewModel by lazy {
        ViewModelProviders.of(this).get(MapViewModel::class.java)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        signInAnonymously()
        initMap()


        menuBtn.setOnClickListener {
            showMenu()
        }

    }
    private fun signInAnonymously(){
        mAuth!!.signInAnonymously()
            .addOnCompleteListener(this){ task ->
                        if (task.isSuccessful) {
                            val user = mAuth!!.currentUser
                            Toast.makeText(baseContext, "Authentication success.",
                                Toast.LENGTH_SHORT).show()
                        } else {

                            Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
            }

    private fun initMap() {
        status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
        if (status != ConnectionResult.SUCCESS) {
            Toast.makeText(this, "No google connection! ", Toast.LENGTH_LONG).show()
        } else {
            val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment?
            mapFragment?.getMapAsync(this)
        }
    }

    override fun onMapReady(mGoogleMap: GoogleMap?) {
        mMap = mGoogleMap!!
        getAllLocations()

      //for enabling location

        enableMyLocation()
        mMap.apply {
            uiSettings.isMyLocationButtonEnabled = true
            uiSettings.isCompassEnabled = true
            mapType = GoogleMap.MAP_TYPE_NORMAL
            uiSettings.isZoomControlsEnabled = true
            uiSettings.isScrollGesturesEnabled = true
            uiSettings.isRotateGesturesEnabled = true
            uiSettings.isMapToolbarEnabled = true
            uiSettings.isIndoorLevelPickerEnabled = true
            setPadding(0, 0, 0, 400)
            setOnMarkerClickListener(this@MainActivity)
            setOnMyLocationButtonClickListener(this@MainActivity)
            setOnMyLocationClickListener(this@MainActivity)
        }

        addLocationBtn.setOnClickListener {

            mMap.setOnMapClickListener {
                mMap.clear()
                mMap.setMinZoomPreference(5.0F)
                drawMarker(it,"HELLO","mirlan")
                val builder = AlertDialog.Builder(this@MainActivity)
                builder.setTitle("Добавление новое место")
                builder.setMessage("Здесь ДПС?")
                builder.setPositiveButton("Отправить"){ _, _ ->

                    viewModel.save(it.latitude, it.longitude,-1)

                    Toast.makeText(applicationContext,"Ваш запрос принят",Toast.LENGTH_SHORT).show()

                }
                builder.setNegativeButton("Отменить"){dialog,which ->
                    Toast.makeText(applicationContext,"Отменено.",Toast.LENGTH_SHORT).show()
                }
                builder.setNeutralButton("Cancel"){_,_ ->
                    Toast.makeText(applicationContext,"You cancelled the dialog.",Toast.LENGTH_SHORT).show()
                }

                val dialog: AlertDialog = builder.create()

                dialog.show()
            }
            }

        back.setOnClickListener {

        }

        mMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            override fun getInfoWindow(marker: Marker): View {

                val v = layoutInflater.inflate(R.layout.info_map, null)
                val ll = LinearLayout(this@MainActivity).apply {

                    setPadding(20, 20, 20, 20)
                    setBackgroundColor(Color.GREEN)
                }

                val textView = TextView(applicationContext)
                textView.text =
                        Html.fromHtml("<b><font color = \"#ffffff\">" + marker.title + ":</font></b>" + marker.snippet)
                ll.addView(textView)

                return ll
            }

            override fun getInfoContents(marker: Marker): View {

                val infoview = layoutInflater.inflate(R.layout.info_window, null)
                val pickMe = infoview.findViewById(R.id.btn) as Button
                pickMe.setOnClickListener {
                    Toast.makeText(applicationContext, "Requested Send", Toast.LENGTH_SHORT).show()
                    goToLocationZoom(74.0, 42.0, 15f)
                }

                return infoview
            }
        })
    }
    private fun getAllLocations(){
        Toast.makeText(this@MainActivity,"hi",Toast.LENGTH_LONG).show()
        viewModel.getPoliceLocations()?.observe(this, Observer{
            it?.forEach {
                val marker = LatLng(it.lat,it.lng)
                drawMarker(marker," "," ")
                Toast.makeText(this,"hi" + it.lat,Toast.LENGTH_LONG).show()
            }
        })
        /*mDatabase = FirebaseDatabase.getInstance().reference.child("locations")
        val locationsListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //val location = dataSnapshot.getValue(PoliceLocation::class.java)
                //val marker = LatLng(location!!.lat,location.lng)
               // drawMarker(marker,location.toString()," ")
               dataSnapshot.children.forEach {
                    val location = it.getValue(PoliceLocation::class.java)
                    val marker = LatLng(location!!.lat,location.lng)
                    drawMarker(marker, location.likes[1.toString()].toString()," ")
                }
             //   Toast.makeText(this@MainActivity,"hi"+location?.id,Toast.LENGTH_LONG).show()
            }}*/

       // mDatabase?.addValueEventListener(locationsListener)
    }
    private fun drawMarker(latLng: LatLng, title: String, snippet: String) {
        val markerOptions = MarkerOptions()
            .title(title)
            .alpha(1.0f)//color
            .position(latLng)
            .snippet(snippet)
        mMap.addMarker(markerOptions)
       // mMap.animateCamera(CameraUpdateFactory.zoomTo(20F), 5000,null)

    }
    override fun onMyLocationClick(location: Location) {
        Toast.makeText(this, "Current location:\n$location", Toast.LENGTH_LONG).show()
    }

    override fun onMyLocationButtonClick(): Boolean {
        return false
    }

    override fun onLocationChanged(location: Location?) {

        var msg = "Updated Location: " + location?.latitude  + " , " +location?.longitude
        val loc = LatLng(location!!.latitude, location.longitude)
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(loc).title("Current Location"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc))
    }

    override fun onMarkerClick(marker: Marker?): Boolean {

        Toast.makeText(
            this,
            marker!!.title +
                    " has been clicked " + " times.",
            Toast.LENGTH_SHORT
        ).show()
        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] === PackageManager.PERMISSION_GRANTED) {
                    enableMyLocation()
                } else {
                   // showDefaultLocation()
                }
                return
            }
        }
    }
    private fun showMenu(){

        val popupMenu = PopupMenu(this, menuBtn)
        popupMenu.setOnMenuItemClickListener {menuItem ->
            when(menuItem.itemId){
                R.id.settings ->{
                    val intent = Intent(this,SettingsActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.about ->{
                    val intent = Intent(this, AboutAppActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.share ->{
                    val sharingIntent = Intent(android.content.Intent.ACTION_SEND)
                    sharingIntent.type = "text/plain"
                    val shareBody = "Here is the share content body"
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here")
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody)
                    startActivity(Intent.createChooser(sharingIntent, "Share via"))
                    true
                }
                R.id.logout ->{
                    true
                }
                else -> false
            }
        }
        popupMenu.inflate(R.menu.menu)
        popupMenu.show()
       /* try {
            val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
            fieldMPopup.isAccessible = false
            val mPopup = fieldMPopup.get(popupMenu)
            mPopup.javaClass
                .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                .invoke(mPopup, true)
        } catch (e: Exception){
           // Log.e("Main", "Error showing menu icons.", e)
        } finally {

        }*/
    }

    private fun goToLocationZoom(lat: Double, lng: Double, zoom: Float) {
        val ll = LatLng(lat, lng)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(ll, zoom)
        mMap.moveCamera(cameraUpdate)
    }

    private fun getMapVisibleRadius(): Double {
        val visibleRegion: VisibleRegion? = mMap.projection?.visibleRegion

        val distanceWidth = FloatArray(1)
        val distanceHeight = FloatArray(1)

        val farRight: LatLng? = visibleRegion?.farRight
        val farLeft: LatLng? = visibleRegion?.farLeft
        val nearRight: LatLng? = visibleRegion?.nearRight
        val nearLeft: LatLng? = visibleRegion?.nearLeft

        Location.distanceBetween(
            (farLeft!!.latitude + nearLeft!!.latitude) / 2,
            farLeft.longitude,
            (farRight!!.latitude + nearRight!!.latitude) / 2,
            farRight.longitude, distanceWidth
        )

        Location.distanceBetween(
            farRight.latitude,
            (farRight.longitude + farLeft.longitude) / 2,
            nearRight.latitude,
            (nearRight.longitude + nearLeft.longitude) / 2,
            distanceHeight
        )

        return Math.sqrt(
            (Math.pow(distanceWidth[0].toString().toDouble(), 2.0))
                    + Math.pow(distanceHeight[0].toString().toDouble(), 2.0)
        ) / 2
    }


    override fun onResume() {
        super.onResume()
        if (status != 0) {
            val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            //startCurrentLocationUpdates()
    }
}

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(REQUEST_CODE_LOCATION)
    private fun enableMyLocation() {
        if (hasLocationPermission()) {
            mMap.isMyLocationEnabled = true
        } else {
            EasyPermissions.requestPermissions(
                this, getString(R.string.location),
                REQUEST_CODE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    private fun hasLocationPermission(): Boolean {
         //if (ActivityCompat.checkSelfPermission(this@MainActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this@MainActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        return EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)


    }

}


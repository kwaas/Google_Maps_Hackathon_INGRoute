package pl.ing.ingroute

import android.app.Dialog
import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.android.PolyUtil
import com.google.maps.errors.ApiException
import com.google.maps.model.DirectionsResult
import com.google.maps.model.DirectionsRoute
import com.google.maps.model.TravelMode
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.content_main.*
import org.joda.time.DateTime
import java.io.IOException
import java.util.concurrent.TimeUnit




class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val TAG = "IngRoute"
    private val overview = 0

    private var googleMap: GoogleMap? = null

    private var lastClicked: com.google.android.gms.maps.model.Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        setSupportActionBar(toolbar)
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fab.setOnClickListener {
            // Toast.makeText(this, getString(R.string.searching_best_route), Toast.LENGTH_SHORT).show()
            Snackbar.make(map_container, getString(R.string.searching_best_route), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            fab.postDelayed({
                findBestRoute()
            }, 250)
        }
    }

    private fun getDirectionsDetails(origin: LatLng, destination: LatLng, mode: TravelMode): DirectionsResult? {
        val now = DateTime()
        try {
            return DirectionsApi.newRequest(getGeoContext())
                    .mode(mode)
                    .origin(com.google.maps.model.LatLng(origin.latitude, origin.longitude))
                    .destination(com.google.maps.model.LatLng(destination.latitude, destination.longitude))
                    .departureTime(now)
                    .await()
        } catch (e: ApiException) {
            e.printStackTrace()
            return null
        } catch (e: InterruptedException) {
            e.printStackTrace()
            return null
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        googleMap.setInfoWindowAdapter(MarkerInfoViewAdapter(this))
        markers(googleMap)
        googleMap.uiSettings.isZoomControlsEnabled = true
        try {
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.ing2))
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
//        googleMap.setPadding(0, 0, resources.getDimensionPixelSize(R.dimen.map_zoom_buttons_right_margin),
//                resources.getDimensionPixelSize(R.dimen.map_zoom_buttons_bottom_margin))

        googleMap.isTrafficEnabled = false

        googleMap.setOnInfoWindowClickListener {
            openDialog()
        }
    }

    private fun markers(googleMap: GoogleMap) {
        val locationManager = LocationManager.newInstance()
        val allLocations = locationManager.getAllDepartmentsLocation()
        allLocations.forEach {
            val marker = googleMap.addMarker(MarkerOptions()
                    .position(it.position.coordinates)
                    .title(it.street))
            marker.tag = it
            //marker.setIcon(Marker.getMarkerIcon("#ff6200"))
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(Marker.getMarkerBitmapFromView(R.drawable.ing_map_marker_2, this)))
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(allLocations[0].position.coordinates, 12f))

        val currentLocation = locationManager.getCurrentLocation(this)

        googleMap.addMarker(MarkerOptions()
                .position(currentLocation.coordinates)
                .title("Twoja lokalizacja"))
                //.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .setIcon(BitmapDescriptorFactory.fromBitmap(Marker.getMarkerBitmapFromView(R.drawable.here_marker, this)))

        googleMap.setOnMarkerClickListener {
            route(googleMap, currentLocation.coordinates, it.position)
            it.showInfoWindow()
            lastClicked = it
            googleMap.isTrafficEnabled = false
            return@setOnMarkerClickListener true
        }
    }

    private fun route(googleMap: GoogleMap, origin: LatLng, destination: LatLng) {
        setupGoogleMapScreenSettings(googleMap)
        val results = getDirectionsDetails(origin, destination, TravelMode.DRIVING)
        if (results != null) {
            addPolyline(results, googleMap)
            positionCamera(results.routes[overview], googleMap)
            //addMarkersToMap(results, googleMap)
        }
    }


    private fun setupGoogleMapScreenSettings(mMap: GoogleMap) {
        mMap.isBuildingsEnabled = true
        mMap.isIndoorEnabled = true
        mMap.isTrafficEnabled = false
        val mUiSettings = mMap.uiSettings
        mUiSettings.isZoomControlsEnabled = true
        mUiSettings.isCompassEnabled = true
        mUiSettings.isMyLocationButtonEnabled = true
        mUiSettings.isScrollGesturesEnabled = true
        mUiSettings.isZoomGesturesEnabled = true
        mUiSettings.isTiltGesturesEnabled = true
        mUiSettings.isRotateGesturesEnabled = true
    }

    private fun addMarkersToMap(results: DirectionsResult, mMap: GoogleMap) {
        mMap.addMarker(MarkerOptions().position(LatLng(results.routes[overview].legs[overview].startLocation.lat, results.routes[overview].legs[overview].startLocation.lng)).title(results.routes[overview].legs[overview].startAddress))
        mMap.addMarker(MarkerOptions().position(LatLng(results.routes[overview].legs[overview].endLocation.lat, results.routes[overview].legs[overview].endLocation.lng)).title(results.routes[overview].legs[overview].startAddress).snippet(getEndLocationTitle(results)))
    }

    private fun positionCamera(route: DirectionsRoute, mMap: GoogleMap) {
        var start = route.legs[overview].startLocation
        var end = route.legs[overview].endLocation
        val centerLat = start.lat - (start.lat - end.lat) / 2
        val centerLng = start.lng - (start.lng - end.lng) / 2
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(centerLat, centerLng), 14f))
    }

    var polyline: Polyline? = null

    private fun addPolyline(results: DirectionsResult, mMap: GoogleMap) {
        polyline?.remove()
        val decodedPath = PolyUtil.decode(results.routes[overview].overviewPolyline.encodedPath)
        polyline = mMap.addPolyline(PolylineOptions().addAll(decodedPath).color(resources.getColor(R.color.P1, null)))
    }

    private fun getEndLocationTitle(results: DirectionsResult): String {
        return "Time :" + results.routes[overview].legs[overview].duration.humanReadable + " Distance :" + results.routes[overview].legs[overview].distance.humanReadable
    }

    private fun getGeoContext(): GeoApiContext {
        val geoApiContext = GeoApiContext()
        return geoApiContext
                .setQueryRateLimit(3)
                .setApiKey(getString(R.string.google_maps_key))
                .setConnectTimeout(10, TimeUnit.SECONDS)
                .setReadTimeout(10, TimeUnit.SECONDS)
                .setWriteTimeout(10, TimeUnit.SECONDS)
    }

    private fun findBestRoute() {
        val locations = LocationManager.newInstance().getAllDepartmentsLocation()
        var bestDestination = locations.first()

        locations.forEach {
            if (it.conditions.getSummaryServiceTime() < bestDestination.conditions.getSummaryServiceTime()) {
                bestDestination = it
            }
        }

        route(googleMap!!, LocationManager.newInstance().getCurrentLocation(context = applicationContext).coordinates,
                bestDestination.position.coordinates)

        lastClicked?.hideInfoWindow()
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.menu_main, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.action_settings -> true
//            else -> super.onOptionsItemSelected(item)
//        }
//    }


    fun openDialog() {

        val alertDialog = AlertDialog.Builder(this).create()

        // Set Custom Title
        val title = TextView(this)
        // Title Properties
        title.text = getString(R.string.confirm_title)
        title.setPadding(10, 10, 10, 10)   // Set Position
        title.gravity = Gravity.CENTER
        title.setTextColor(Color.BLACK)
        title.textSize = 20f
        alertDialog.setCustomTitle(title)

        // Set Message
        val msg = TextView(this)
        // Message Properties
        msg.text = "I am a Custom Dialog Box. \n Please Customize me."
        msg.gravity = Gravity.CENTER_HORIZONTAL
        msg.setTextColor(Color.BLACK)
        alertDialog.setView(msg)

        // Set Button
        // you can more buttons
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", DialogInterface.OnClickListener { dialog, which ->
            // Perform Action on Button
        })

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", DialogInterface.OnClickListener { dialog, which ->
            // Perform Action on Button
        })

        Dialog(applicationContext)
        alertDialog.show()

        // Set Properties for OK Button
        val okBT = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL)
        val neutralBtnLP = okBT.getLayoutParams() as LinearLayout.LayoutParams
        neutralBtnLP.gravity = Gravity.FILL_HORIZONTAL
        okBT.setPadding(50, 10, 10, 10)   // Set Position
        okBT.setTextColor(Color.BLUE)
        okBT.setLayoutParams(neutralBtnLP)

        val cancelBT = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        val negBtnLP = okBT.getLayoutParams() as LinearLayout.LayoutParams
        negBtnLP.gravity = Gravity.FILL_HORIZONTAL
        cancelBT.setTextColor(Color.RED)
        cancelBT.setLayoutParams(negBtnLP)
    }
}

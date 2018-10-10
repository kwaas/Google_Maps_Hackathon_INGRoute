package pl.ing.ingroute

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.android.PolyUtil
import com.google.maps.errors.ApiException
import com.google.maps.model.DirectionsResult
import com.google.maps.model.DirectionsRoute
import com.google.maps.model.TravelMode
import org.joda.time.DateTime
import java.io.IOException
import java.util.concurrent.TimeUnit


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val overview = 0

    private var PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 104

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun getDirectionsDetails(origin: String, destination: String, mode: TravelMode): DirectionsResult? {
        val now = DateTime()
        try {
            return DirectionsApi.newRequest(getGeoContext())
                    .mode(mode)
                    .origin(origin)
                    .destination(destination)
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

        setupGoogleMapScreenSettings(googleMap)
        val results = getDirectionsDetails("al. Jerozolimskie 54, 00-001 Warszawa", "Warszawa Al.Jerozolimskie 32 00-024",
                TravelMode.DRIVING)
        if (results != null) {
            addPolyline(results, googleMap)
            positionCamera(results.routes[overview], googleMap)
            addMarkersToMap(results, googleMap)
        }
    }

    private fun setupGoogleMapScreenSettings(mMap: GoogleMap) {
        mMap.isBuildingsEnabled = true
        mMap.isIndoorEnabled = true
        mMap.isTrafficEnabled = true
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(route.legs[overview].startLocation.lat, route.legs[overview].startLocation.lng), 12f))
    }

    private fun addPolyline(results: DirectionsResult, mMap: GoogleMap) {
        val decodedPath = PolyUtil.decode(results.routes[overview].overviewPolyline.encodedPath)
        mMap.addPolyline(PolylineOptions().addAll(decodedPath))
    }

    private fun getEndLocationTitle(results: DirectionsResult): String {
        return "Time :" + results.routes[overview].legs[overview].duration.humanReadable + " Distance :" + results.routes[overview].legs[overview].distance.humanReadable
    }

    private fun getGeoContext(): GeoApiContext {
        val geoApiContext = GeoApiContext()
        return geoApiContext
                .setQueryRateLimit(3)
                .setApiKey(getString(R.string.google_maps_key))
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS)
    }
}

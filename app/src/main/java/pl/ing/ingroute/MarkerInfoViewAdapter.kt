package pl.ing.ingroute

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker


class MarkerInfoViewAdapter(val context: Context) : GoogleMap.InfoWindowAdapter {

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }

    override fun getInfoContents(marker: Marker): View {
        val view = (context as Activity).layoutInflater
                .inflate(R.layout.marker_info_view, null)

        val location = marker.tag as Location?
        Log.d("MarkerInfoViewAdapter", location?.city + " " + location?.street)

        val cityTv: TextView = view.findViewById(R.id.city)
        cityTv.text = location?.city

        val streetTv: TextView = view.findViewById(R.id.street)
        streetTv.text = location?.street

        val openHoursTv: TextView = view.findViewById(R.id.openHours)
        openHoursTv.text = location?.officeTime.toString()

        val serviceTimeTv: TextView = view.findViewById(R.id.serviceTime)
        serviceTimeTv.text = context.getString(R.string.service_time, location?.conditions?.waitingTimeInMinutes)

        val routeTimeTv: TextView = view.findViewById(R.id.routeTime)
        routeTimeTv.text = context.getString(R.string.route_time, location?.conditions?.routeTimeInMinutes)

        return view
    }
}
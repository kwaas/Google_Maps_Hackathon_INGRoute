package pl.ing.ingroute

import android.graphics.Color
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory


/**
 * Created by marcin.kwasniak on 11.10.2018
 */
class Marker {
    companion object {
        fun getMarkerIcon(color: String): BitmapDescriptor {
            val hsv = FloatArray(3)
            Color.colorToHSV(Color.parseColor(color), hsv)
            return BitmapDescriptorFactory.defaultMarker(hsv[0])
        }
    }
}
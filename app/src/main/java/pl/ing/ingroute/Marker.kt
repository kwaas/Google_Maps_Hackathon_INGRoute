package pl.ing.ingroute

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.support.annotation.DrawableRes
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
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

        fun getMarkerBitmapFromView(@DrawableRes resId: Int, context: Context): Bitmap {

            val customMarkerView = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.ing_marker, null)
            val markerImageView = customMarkerView.findViewById(R.id.profile_image) as ImageView
            markerImageView.setImageResource(resId)
            customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight())
            customMarkerView.buildDrawingCache()
            val returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                    Bitmap.Config.ARGB_8888)
            val canvas = Canvas(returnedBitmap)
            canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN)
            val drawable = customMarkerView.getBackground()
            if (drawable != null)
                drawable!!.draw(canvas)
            customMarkerView.draw(canvas)
            return returnedBitmap
        }
    }
}
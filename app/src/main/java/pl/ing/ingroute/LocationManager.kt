package pl.ing.ingroute

import android.content.Context
import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import java.util.*

/**
 * Created by marcin.kwasniak on 10.10.2018
 */
class LocationManager {

    val locations: List<Location> = listOf(
            Location(id = 11811, city = "Warszawa", street = "Al.Jerozolimskie 32", zipCode = "00-024", province = "mazowieckie",
                    position = Position(LatLng(52.2313678, 21.0179422)), conditions = Conditions(23)),

            Location(id = 15121, city = "Warszawa", street = "Zwycięzców 42", zipCode = "03-938", province = "mazowieckie",
                    position = Position(LatLng(52.2301568, 21.0506105)), conditions = Conditions(7)),

            Location(id = 15881, city = "Warszawa", street = "Mickiewicza 12", zipCode = "05-200", province = "mazowieckie",
                    position = Position(LatLng(52.2210903, 21.0181425)), conditions = Conditions(1)))

    companion object {
        fun newInstance(): LocationManager {
            return LocationManager()
        }
    }

    fun getAllDepartmentsLocation(): List<Location> {
        return locations
    }

    fun getCurrentLocation(context: Context) : Position {
        val locationName = "al. Jerozolimskie 54, 00-001 Warszawa"

        val geocoder = Geocoder(context, Locale.getDefault())
        val address = geocoder.getFromLocationName(locationName, 1)

        return Position(LatLng(address.first().latitude, address.first().longitude))
    }
}
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
                    position = Position(LatLng(52.2313678, 21.0179422)), conditions = Conditions(23, 10)),

            Location(id = 15121, city = "Warszawa", street = "Zwycięzców 42", zipCode = "03-938", province = "mazowieckie",
                    position = Position(LatLng(52.2301568, 21.0506105)), conditions = Conditions(7, 9)),

            Location(id = 15881, city = "Warszawa", street = "Mickiewicza 12", zipCode = "05-200", province = "mazowieckie",
                    position = Position(LatLng(52.2210903, 21.0181425)), conditions = Conditions(1, 11)),

            Location(id = 14661, city = "Warszawa", street = "Al. Jerozolimskie 54", zipCode = "00-024", province = "mazowieckie",
                    position = Position(LatLng(52.2288214, 21.0031637)), conditions = Conditions(12, 8)),

            Location(id = 11761, city = "Warszawa", street = "Aleja Jana Pawła II 29", zipCode = "00-867", province = "mazowieckie",
                    position = Position(LatLng(52.2325423, 20.9989723)), conditions = Conditions(45, 12)),

            Location(id = 15111, city = "Warszawa", street = "Marszałkowska 85", zipCode = "00-683", province = "mazowieckie",
                    position = Position(LatLng(52.2270487, 21.0132283)), conditions = Conditions(5, 9)),

            Location(id = 15491, city = "Warszawa", street = "Słomińskiego 7", zipCode = "00-195", province = "mazowieckie",
                    position = Position(LatLng(52.2562417, 20.9863664)), conditions = Conditions(3, 11))
    )

    companion object {
        fun newInstance(): LocationManager {
            return LocationManager()
        }
    }

    fun getAllDepartmentsLocation(): List<Location> {
        return locations
    }

    fun getCurrentLocation(context: Context): Position {
        val locationName = "ul. Emilii Plater 53, 00-113 Warszawa"

        val geocoder = Geocoder(context, Locale.getDefault())
        val address = geocoder.getFromLocationName(locationName, 1)

        return Position(LatLng(address.first().latitude, address.first().longitude))
    }
}
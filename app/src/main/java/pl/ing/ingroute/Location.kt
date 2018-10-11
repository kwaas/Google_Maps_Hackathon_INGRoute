package pl.ing.ingroute

import com.google.android.gms.maps.model.LatLng

/**
 * Created by marcin.kwasniak on 10.10.2018
 */
data class Location(val id: Int,
                    val city: String,
                    val kind: String = "O",
                    val street: String,
                    val zipCode: String,
                    val province: String,
                    val officeTime: Time = Time("10:00", "17:00", true, false),
                    val position: Position = Position(LatLng(1.0, 1.0)),
                    val conditions: Conditions)

data class Time(val start: String, val stop: String, val saturday: Boolean, val sunday: Boolean) {
    override fun toString() : String {
        return "$start - $stop"
    }
}
data class Position(val coordinates: LatLng)
data class Conditions(val waitingTimeInMinutes: Int, val routeTimeInMinutes: Int = 10) {
    fun getSummaryServiceTime() = waitingTimeInMinutes + routeTimeInMinutes
}
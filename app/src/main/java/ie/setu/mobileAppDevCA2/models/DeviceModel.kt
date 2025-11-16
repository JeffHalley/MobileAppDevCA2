package ie.setu.mobileAppDevCA2.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DeviceModel(
    var id: Long = 0,
    var title: String = "",
    var description: String = "",
    var status: Boolean = false,
    var activatedAt: String = "",
    var sensorFamily: String = "",
    var image: String = "",
    var lat: Double = 0.0,
    var lng: Double = 0.0,
    var zoom: Float = 0f
) : Parcelable


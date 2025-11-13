package ie.setu.mobileAppDevCA2.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DeviceModel(
    var id: Long = 0,
    var title: String = "",
    var description: String = "",
    var status: Boolean = false,
    var activatedAt: String = "",
    var sensorFamily: String = ""
) : Parcelable
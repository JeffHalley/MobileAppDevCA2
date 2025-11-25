package ie.setu.mobileAppDevCA2.activities

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import ie.setu.mobileAppDevCA2.main.MainApp
import ie.setu.mobileAppDevCA2.models.DeviceModel

class DeviceMapsPresenter(private val view: DeviceMapsView) : GoogleMap.OnMarkerClickListener {

    private val app: MainApp = view.application as MainApp
    private lateinit var map: GoogleMap

    fun configureMap(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true

        app.devices.findAll().forEach { device ->
            val loc = LatLng(device.lat, device.lng)
            val options = MarkerOptions()
                .title(device.title)
                .position(loc)

            map.addMarker(options)?.tag = device.id
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, device.zoom))
        }

        map.setOnMarkerClickListener(this)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val tag = marker.tag as Long
        val device = app.devices.findById(tag)

        if (device != null) {
            view.showDeviceInfo(device)
        }

        return false
    }
}

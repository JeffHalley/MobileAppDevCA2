package ie.setu.mobileAppDevCA2.activities

import android.app.Activity
import android.content.Intent
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class EditLocationPresenter(private val view: EditLocationView) {

    var lat: Double = 0.0
    var lng: Double = 0.0
    var zoom: Float = 15f

    private lateinit var map: GoogleMap
    private lateinit var marker: Marker

    fun initFromIntent() {
        lat = view.intent.getDoubleExtra("lat", 52.245696)
        lng = view.intent.getDoubleExtra("lng", -7.139102)
        zoom = view.intent.getFloatExtra("zoom", 15f)
    }

    fun configureMap(googleMap: GoogleMap) {
        map = googleMap

        val loc = LatLng(lat, lng)

        val options = MarkerOptions()
            .title("Placemark")
            .snippet("GPS: ${loc.latitude}, ${loc.longitude}")
            .draggable(true)
            .position(loc)

        marker = map.addMarker(options)!!
        map.setOnMarkerDragListener(view)
        map.setOnMarkerClickListener { m ->
            m.snippet = "GPS: ${m.position.latitude}, ${m.position.longitude}"
            m.showInfoWindow()
            false
        }

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, zoom))
    }

    fun updateOnDrag(marker: Marker) {
        marker.snippet = "GPS: ${marker.position.latitude}, ${marker.position.longitude}"
        marker.showInfoWindow()
    }

    fun saveDragEnd(marker: Marker) {
        lat = marker.position.latitude
        lng = marker.position.longitude
        zoom = map.cameraPosition.zoom
        marker.snippet = "GPS: $lat, $lng"
        marker.showInfoWindow()
    }

    fun returnUpdatedLocation() {
        val resultIntent = Intent()
        resultIntent.putExtra("lat", lat)
        resultIntent.putExtra("lng", lng)
        resultIntent.putExtra("zoom", zoom)

        view.setResult(Activity.RESULT_OK, resultIntent)
        view.finish()
    }
}

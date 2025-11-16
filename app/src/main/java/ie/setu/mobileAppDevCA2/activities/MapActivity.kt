package ie.setu.mobileAppDevCA2.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.addCallback
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import ie.setu.mobileAppDevCA2.R
import ie.setu.mobileAppDevCA2.databinding.ActivityMapBinding

class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerDragListener {

    private lateinit var map: GoogleMap
    private lateinit var marker: Marker
    private lateinit var binding: ActivityMapBinding

    // Device coordinates passed via intent
    private var lat: Double = 0.0
    private var lng: Double = 0.0
    private var zoom: Float = 15f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get coordinates from intent extras
        lat = intent.getDoubleExtra("lat", 52.245696)
        lng = intent.getDoubleExtra("lng", -7.139102)
        zoom = intent.getFloatExtra("zoom", 15f)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Handle back press: return updated coordinates
        onBackPressedDispatcher.addCallback(this) {
            val resultIntent = Intent()
            resultIntent.putExtra("lat", lat)
            resultIntent.putExtra("lng", lng)
            resultIntent.putExtra("zoom", zoom)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val loc = LatLng(lat, lng)
        val options = MarkerOptions()
            .title("Placemark")
            .snippet("GPS: ${loc.latitude}, ${loc.longitude}")
            .draggable(true)
            .position(loc)
        marker = map.addMarker(options)!!
        map.setOnMarkerDragListener(this)

        // Update snippet on click to reflect current position
        map.setOnMarkerClickListener { m ->
            m.snippet = "GPS: ${m.position.latitude}, ${m.position.longitude}"
            m.showInfoWindow()
            false
        }

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, zoom))
    }

    override fun onMarkerDragStart(marker: Marker) {
        // Optional: do something when drag starts
    }

    override fun onMarkerDrag(marker: Marker) {
        // Update snippet live while dragging
        marker.snippet = "GPS: ${marker.position.latitude}, ${marker.position.longitude}"
        marker.showInfoWindow()
    }

    override fun onMarkerDragEnd(marker: Marker) {
        // Save new position & zoom
        lat = marker.position.latitude
        lng = marker.position.longitude
        zoom = map.cameraPosition.zoom

        // Update snippet
        marker.snippet = "GPS: $lat, $lng"
        marker.showInfoWindow()
    }
}

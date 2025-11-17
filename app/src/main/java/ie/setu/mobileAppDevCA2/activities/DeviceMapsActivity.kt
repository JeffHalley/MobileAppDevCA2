package ie.setu.mobileAppDevCA2.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import ie.setu.mobileAppDevCA2.databinding.ActivityDeviceMapsBinding
import ie.setu.mobileAppDevCA2.databinding.ContentDeviceMapsBinding
import ie.setu.mobileAppDevCA2.main.MainApp

class DeviceMapsActivity : AppCompatActivity(), GoogleMap.OnMarkerClickListener {

    private lateinit var binding: ActivityDeviceMapsBinding
    private lateinit var contentBinding: ContentDeviceMapsBinding
    lateinit var map: GoogleMap
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDeviceMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        app = application as MainApp
        contentBinding = ContentDeviceMapsBinding.bind(binding.root)
        contentBinding.mapView.onCreate(savedInstanceState)

        contentBinding.mapView.getMapAsync {
            map = it
            configureMap()
        }
    }

    private fun configureMap() {
        map.uiSettings.isZoomControlsEnabled = true
        app.devices.findAll().forEach {
            val loc = LatLng(it.lat, it.lng)
            val options = MarkerOptions().title(it.title).position(loc)
            map.addMarker(options)?.tag = it.id
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, it.zoom))
            map.setOnMarkerClickListener(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        contentBinding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        contentBinding.mapView.onLowMemory()
    }

    override fun onPause() {
        super.onPause()
        contentBinding.mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        contentBinding.mapView.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        contentBinding.mapView.onSaveInstanceState(outState)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        contentBinding.currentTitle.text = marker.title

        return false
    }

}


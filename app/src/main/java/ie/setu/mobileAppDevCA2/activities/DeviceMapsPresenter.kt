package ie.setu.mobileAppDevCA2.activities

import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.android.volley.Request
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import ie.setu.mobileAppDevCA2.main.MainApp
import ie.setu.mobileAppDevCA2.models.DeviceModel
import org.json.JSONObject

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
        val id = marker.tag as Long
        val device = app.devices.findById(id)

        if (device != null) {
            view.showDeviceInfo(device)
            fetchWeatherFor(device)
        }

        return false
    }


    fun fetchWeatherFor(device: DeviceModel) {
        val url =
            "https://api.open-meteo.com/v1/forecast?latitude=${device.lat}&longitude=${device.lng}&current_weather=true"

        val queue = Volley.newRequestQueue(view)
        val request = StringRequest(
            Request.Method.GET, url,
            { response ->
                val json = JSONObject(response)
                val weather = json.getJSONObject("current_weather")

                val temp = weather.getDouble("temperature")
                val wind = weather.getDouble("windspeed")

                view.showWeatherInfo(temp, wind)
            },
            { error ->
                view.showWeatherInfoError(error.toString())
            })

        queue.add(request)
    }

}
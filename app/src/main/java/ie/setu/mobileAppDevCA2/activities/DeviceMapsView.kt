package ie.setu.mobileAppDevCA2.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import com.google.android.gms.maps.GoogleMap
import ie.setu.mobileAppDevCA2.R
import ie.setu.mobileAppDevCA2.databinding.ActivityDeviceMapsBinding
import ie.setu.mobileAppDevCA2.databinding.ContentDeviceMapsBinding
import ie.setu.mobileAppDevCA2.models.DeviceModel

class DeviceMapsView : AppCompatActivity() {

    private lateinit var binding: ActivityDeviceMapsBinding
    private lateinit var contentBinding: ContentDeviceMapsBinding
    private lateinit var presenter: DeviceMapsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDeviceMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        presenter = DeviceMapsPresenter(this)

        contentBinding = ContentDeviceMapsBinding.bind(binding.root)
        contentBinding.mapView.onCreate(savedInstanceState)

        contentBinding.mapView.getMapAsync { googleMap ->
            presenter.configureMap(googleMap)
        }
    }

    fun showDeviceInfo(device: DeviceModel) {
        contentBinding.currentTitle.text = device.title
        contentBinding.currentDescription.text = device.description

        if (device.image.isNotEmpty()) {
            Picasso.get()
                .load(device.image)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(contentBinding.currentImage)
        } else {
            contentBinding.currentImage.setImageResource(R.drawable.ic_launcher_foreground)
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

    fun showWeatherInfo(
        temp: Double,
        wind: Double,
        windDir: Double,
        humidity: Double,
        pressure: Double,
        cloudcover: Double,
        uv: Double
    ) {
        val formatted = getString(
            R.string.weather_info,
            temp, wind, windDir, humidity, pressure, cloudcover, uv
        )
        contentBinding.currentWeather.text = formatted
    }

    fun showWeatherInfoError(errorMessage: String) {
        contentBinding.currentWeather.text = getString(R.string.weather_error, errorMessage)
    }






}

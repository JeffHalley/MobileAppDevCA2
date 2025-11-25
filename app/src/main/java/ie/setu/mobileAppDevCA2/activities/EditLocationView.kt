package ie.setu.mobileAppDevCA2.activities

import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import ie.setu.mobileAppDevCA2.R
import ie.setu.mobileAppDevCA2.databinding.ActivityMapBinding

class EditLocationView :
    AppCompatActivity(),
    OnMapReadyCallback,
    GoogleMap.OnMarkerDragListener {

    private lateinit var binding: ActivityMapBinding
    lateinit var presenter: EditLocationPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = EditLocationPresenter(this)
        presenter.initFromIntent()

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        onBackPressedDispatcher.addCallback(this) {
            presenter.returnUpdatedLocation()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        presenter.configureMap(map)
    }

    override fun onMarkerDragStart(marker: Marker) {

    }

    override fun onMarkerDrag(marker: Marker) {
        presenter.updateOnDrag(marker)
    }

    override fun onMarkerDragEnd(marker: Marker) {
        presenter.saveDragEnd(marker)
    }
}

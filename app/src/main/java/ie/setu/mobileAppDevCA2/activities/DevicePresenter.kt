package ie.setu.mobileAppDevCA2.activities

import android.app.Activity.RESULT_OK
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import ie.setu.mobileAppDevCA2.main.MainApp
import ie.setu.mobileAppDevCA2.models.DeviceModel
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class DevicePresenter(private val view: DeviceView) {

    var device = DeviceModel()
    var app: MainApp = view.application as MainApp
    var edit = false

    private lateinit var imageIntentLauncher : ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var mapIntentLauncher : ActivityResultLauncher<Intent>

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    init {
        if (view.intent.hasExtra("device_edit")) {
            edit = true
            device = view.intent.extras?.getParcelable("device_edit")!!
            view.showDevice(device)
        }
        registerImagePickerCallback()
        registerMapCallback()
    }

    fun doSelectImage() {
        val request = PickVisualMediaRequest.Builder()
            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
            .build()
        imageIntentLauncher.launch(request)
    }

    fun doPickDate() {
        view.showDatePicker { selected ->
            device.activatedAt = dateFormat.format(selected.time)
            view.updateDateDisplay(device.activatedAt)
        }
    }

    fun doSetLocation() {
        val launcherIntent = Intent(view, EditLocationView::class.java)
            .putExtra("lat", device.lat)
            .putExtra("lng", device.lng)
            .putExtra("zoom", device.zoom)

        mapIntentLauncher.launch(launcherIntent)
    }

    fun cacheDevice(title: String, desc: String) {
        device.title = title
        device.description = desc
    }

    fun doAddOrSave(title: String, desc: String, family: String, status: Boolean) {
        device.title = title
        device.description = desc
        device.sensorFamily = family
        device.status = status

        if (edit) app.devices.update(device)
        else app.devices.create(device)

        view.setResult(RESULT_OK)
        view.finish()
    }

    fun doCancel() {
        view.finish()
    }

    fun doDelete() {
        app.devices.delete(device)
        view.setResult(99)
        view.finish()
    }

    private fun registerImagePickerCallback() {
        imageIntentLauncher = view.registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            if (uri == null) return@registerForActivityResult
            try {
                view.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                device.image = uri.toString()
                view.updateImage(uri)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun registerMapCallback() {
        mapIntentLauncher =
            view.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK && result.data != null) {
                    val lat = result.data!!.getDoubleExtra("lat", device.lat)
                    val lng = result.data!!.getDoubleExtra("lng", device.lng)
                    val zoom = result.data!!.getFloatExtra("zoom", device.zoom)

                    device.lat = lat
                    device.lng = lng
                    device.zoom = zoom

                    Timber.i("Updated device location: lat=$lat, lng=$lng, zoom=$zoom")
                }
            }
    }
}

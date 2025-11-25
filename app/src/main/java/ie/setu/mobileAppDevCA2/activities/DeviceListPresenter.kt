package ie.setu.mobileAppDevCA2.activities

import android.app.AlertDialog
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import ie.setu.mobileAppDevCA2.main.MainApp
import ie.setu.mobileAppDevCA2.models.DeviceModel

class DeviceListPresenter(private val view: DeviceListView) {

    private val app: MainApp = view.application as MainApp

    fun loadDevices() = app.devices.findAll()

    fun doAddDevice(launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent(view, DeviceView::class.java)
        launcher.launch(intent)
    }

    fun doEditDevice(device: DeviceModel, launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent(view, DeviceView::class.java)
        intent.putExtra("device_edit", device)
        launcher.launch(intent)
    }

    fun doOpenMap(launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent(view, DeviceMapsView::class.java)
        launcher.launch(intent)
    }

    fun doDeleteAll(onDone: () -> Unit) {
        AlertDialog.Builder(view)
            .setTitle("Delete All Devices")
            .setMessage("Are you sure?")
            .setPositiveButton("Yes") { _, _ ->
                app.devices.deleteAll()
                onDone()
            }
            .setNegativeButton("No", null)
            .show()
    }

    fun deleteDevice(device: DeviceModel): Int {
        val list = app.devices.findAll()
        val pos = list.indexOf(device)
        if (pos != -1) {
            app.devices.delete(device)
        }
        return pos
    }

    fun deviceCount() = app.devices.findAll().size
}

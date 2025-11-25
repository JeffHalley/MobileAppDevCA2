package ie.setu.mobileAppDevCA2.models

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import timber.log.Timber.i
import java.io.File

var lastId = 0L
internal fun getId(): Long = lastId++

class DeviceMemStore(private val context: Context) : DeviceStore {

    private val gson = Gson()
    private val jsonFile = File(context.filesDir, "devices.json")
    private val listType = object : TypeToken<ArrayList<DeviceModel>>() {}.type

    var devices = ArrayList<DeviceModel>()

    override fun findAll(): List<DeviceModel> = devices

    override fun create(device: DeviceModel) {
        device.id = getId()
        devices.add(device)
        save()
        logAll()
    }

    override fun update(device: DeviceModel) {
        val foundDevice = devices.find { it.id == device.id }
        if (foundDevice != null) {
            foundDevice.title = device.title
            foundDevice.description = device.description
            foundDevice.status = device.status
            foundDevice.activatedAt = device.activatedAt
            foundDevice.sensorFamily = device.sensorFamily
            foundDevice.image = device.image
            foundDevice.lat = device.lat
            foundDevice.lng = device.lng
            foundDevice.zoom = device.zoom
            save()
            logAll()
        }
    }

    override fun delete(device: DeviceModel) {
        devices.removeAll { it.id == device.id }
        save()
        logAll()
    }

    fun deleteAll() {
        devices.clear()
        save()
        logAll()
    }

    fun load() {
        i("Starting load of devices...")

        if (jsonFile.exists()) {
            i("Internal JSON file exists at: ${jsonFile.absolutePath}")

            val jsonString = jsonFile.readText()
            if (jsonString.isBlank() || jsonString.trim() == "[]") {
                i("Internal JSON file is empty. Loading from assets instead.")
                loadFromAssets()
            } else {
                try {
                    devices = gson.fromJson(jsonString, listType)
                    i("Loaded ${devices.size} devices from internal JSON")
                } catch (ex: Exception) {
                    i("Error parsing internal JSON: ${ex.message}. Falling back to assets.")
                    loadFromAssets()
                }
            }
        } else {
            i("Internal JSON file does not exist. Loading from assets")
            loadFromAssets()
        }
    }

    fun loadFromJson(jsonString: String) {
        try {
            devices = gson.fromJson(jsonString, listType)
            i("Loaded ${devices.size} devices from provided JSON")
            save() //save to internal storage
        } catch (ex: Exception) {
            i("Error parsing JSON: ${ex.message}. Falling back to assets.")
            loadFromAssets()
        }
    }


    private fun loadFromAssets() {
        try {
            i("Opening devices.json from assets...")
            val assetJson = context.assets.open("devices.json").bufferedReader().use { it.readText() }
            if (assetJson.isBlank()) {
                i("Error: assets/devices.json is empty!")
                return
            }

            devices = gson.fromJson(assetJson, listType)
            i("Seeded ${devices.size} devices from assets")

            // Save to internal storage for future edits
            save()
        } catch (ex: Exception) {
            i("Error reading devices from assets: ${ex.message}")
        }
    }


    private fun save() {
        val json = gson.toJson(devices)
        jsonFile.writeText(json)
        i("Saved ${devices.size} devices to ${jsonFile.absolutePath}")
    }

    private fun logAll() {
        devices.forEach { i("Device: $it") }
    }

    override fun findById(id:Long) : DeviceModel? {
        val foundDevice: DeviceModel? = devices.find { it.id == id }
        return foundDevice
    }


}

package ie.setu.mobileAppDevCA2.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import ie.setu.mobileAppDevCA2.R
import ie.setu.mobileAppDevCA2.databinding.ActivityDeviceBinding
import ie.setu.mobileAppDevCA2.helpers.showImagePicker
import ie.setu.mobileAppDevCA2.main.MainApp
import ie.setu.mobileAppDevCA2.models.DeviceModel
import timber.log.Timber.i
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.net.toUri
import ie.setu.mobileAppDevCA2.activities.MapActivity


class DeviceActivity : AppCompatActivity() {

    private lateinit var imageIntentLauncher : ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var mapIntentLauncher : ActivityResultLauncher<Intent>

    private fun registerMapCallback() {
        mapIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK && result.data != null) {
                    i("Got Location ${result.data.toString()}")
                    // Get updated coordinates from MapActivity
                    val lat = result.data!!.getDoubleExtra("lat", device.lat)
                    val lng = result.data!!.getDoubleExtra("lng", device.lng)
                    val zoom = result.data!!.getFloatExtra("zoom", device.zoom)

                    device.lat = lat
                    device.lng = lng
                    device.zoom = zoom

                    i("Updated device location: lat=$lat, lng=$lng, zoom=$zoom")
                }
            }
    }




    private fun registerImagePickerCallback() {
        imageIntentLauncher = registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            if (uri == null) return@registerForActivityResult
            try {
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                device.image = uri.toString()
                Picasso.get()
                    .load(uri)
                    .into(binding.deviceImage)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private lateinit var binding: ActivityDeviceBinding
    var device = DeviceModel()
    lateinit var app: MainApp

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeviceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerImagePickerCallback()
        registerMapCallback()

        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)

        val editing = intent.hasExtra("device_edit")

        binding.toolbarAdd.title =
            if (editing) getString(R.string.update_device)
            else getString(R.string.button_addDevice)

        binding.btnAdd.text =
            if (editing) getString(R.string.update_device)
            else getString(R.string.button_addDevice)

        binding.chooseImage.text =
            if (editing) getString(R.string.change_device_image)
            else getString(R.string.button_addImage)

        app = application as MainApp

        // Populate dropdown (spinner)
        val families = listOf("Temperature", "pH", "Ultrasonic", "Lighting", "Motion")
        val adapter = android.widget.ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            families
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categorySpinner.adapter = adapter

        // If editing an existing device
        if (intent.hasExtra("device_edit")) {
            device = intent.extras?.getParcelable("device_edit")!!
            i("Device to edit: $device")
            i("Device image: ${device.image}")

            if (device.image.isNotEmpty()) {
                Picasso.get()
                    .load(device.image.toUri())
                    .into(binding.deviceImage)
            } else {
                binding.deviceImage.setImageResource(R.drawable.placeholder)
            }

            binding.deviceTitle.setText(device.title)
            binding.deviceDescription.setText(device.description)
            binding.checkActive.isChecked = device.status
            binding.dateText.text = device.activatedAt.ifEmpty { "Select date" }
            val position = families.indexOf(device.sensorFamily)
            if (position >= 0) binding.categorySpinner.setSelection(position)
        } else {
        }

        // Date picker
        binding.btnPickDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val dialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, dayOfMonth)
                    val formattedDate = dateFormat.format(selectedDate.time)
                    device.activatedAt = formattedDate
                    binding.dateText.text = formattedDate
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            dialog.show()
        }


        binding.chooseImage.setOnClickListener {
            // showImagePicker(imageIntentLauncher,this)
            val request = PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                .build()
            imageIntentLauncher.launch(request)
        }



        binding.deviceLocation.setOnClickListener {
            val launcherIntent = Intent(this, MapActivity::class.java).apply {
                putExtra("lat", device.lat)
                putExtra("lng", device.lng)
                putExtra("zoom", device.zoom)
            }
            mapIntentLauncher.launch(launcherIntent)
        }




        // Save / update device
        binding.btnAdd.setOnClickListener {
            device.title = binding.deviceTitle.text.toString()
            device.description = binding.deviceDescription.text.toString()
            device.status = binding.checkActive.isChecked
            device.sensorFamily = binding.categorySpinner.selectedItem.toString()

            if (device.activatedAt.isEmpty()) {
                // Set current date if user didn’t choose one
                device.activatedAt = dateFormat.format(Date())
            }

            if (device.title.isNotEmpty() && device.description.isNotEmpty()) {
                if (intent.hasExtra("device_edit")) {
                    app.devices.update(device)
                    i("Updated device: $device")
                } else {
                    app.devices.create(device)
                    i("Created new device: $device")
                }
                setResult(RESULT_OK)
                finish()
            } else {
                Snackbar.make(it, "Incomplete Data", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_device, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_cancel -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}

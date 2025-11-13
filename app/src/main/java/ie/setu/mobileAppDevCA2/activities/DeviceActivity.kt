package ie.setu.mobileAppDevCA2.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import ie.setu.mobileAppDevCA2.R
import ie.setu.mobileAppDevCA2.databinding.ActivityDeviceBinding
import ie.setu.mobileAppDevCA2.main.MainApp
import ie.setu.mobileAppDevCA2.models.DeviceModel
import timber.log.Timber.i
import java.text.SimpleDateFormat
import java.util.*

class DeviceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeviceBinding
    var device = DeviceModel()
    lateinit var app: MainApp

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeviceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)
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
            binding.deviceTitle.setText(device.title)
            binding.deviceDescription.setText(device.description)
            binding.checkActive.isChecked = device.status
            binding.dateText.text = device.activatedAt.ifEmpty { "Select date" }
            val position = families.indexOf(device.sensorFamily)
            if (position >= 0) binding.categorySpinner.setSelection(position)
            binding.btnAdd.setText(R.string.update_device)
        } else {
            binding.btnAdd.setText(R.string.button_addDevice)
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

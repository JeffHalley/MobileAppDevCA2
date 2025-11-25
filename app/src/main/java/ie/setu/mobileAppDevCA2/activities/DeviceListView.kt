package ie.setu.mobileAppDevCA2.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ie.setu.mobileAppDevCA2.R
import ie.setu.mobileAppDevCA2.adapters.DeviceAdapter
import ie.setu.mobileAppDevCA2.adapters.DeviceListener
import ie.setu.mobileAppDevCA2.databinding.ActivityDeviceListBinding
import ie.setu.mobileAppDevCA2.main.MainApp
import ie.setu.mobileAppDevCA2.models.DeviceModel

class DeviceListView : AppCompatActivity(), DeviceListener {

    lateinit var app: MainApp
    private lateinit var binding: ActivityDeviceListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeviceListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = title
        setSupportActionBar(binding.toolbar)

        app = application as MainApp

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = DeviceAdapter(app.devices.findAll(), this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_add -> {
                val launcherIntent = Intent(this, DeviceView::class.java)
                getResult.launch(launcherIntent)
            }
            R.id.item_map -> {
                val launcherIntent = Intent(this, DeviceMapsView::class.java)
                mapIntentLauncher.launch(launcherIntent)

            }

            R.id.item_delete_all -> {
                AlertDialog.Builder(this)
                    .setTitle("Delete All Devices")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Yes") { _, _ ->
                        app.devices.deleteAll()
                        val adapter = binding.recyclerView.adapter

                        adapter?.notifyDataSetChanged()
                    }
                    .setNegativeButton("No", null)
                    .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDeviceDelete(device: DeviceModel) {

        AlertDialog.Builder(this)
            .setTitle("Delete Device")
            .setMessage("Are you sure you want to delete ${device.title}?")
            .setPositiveButton("Yes") { _, _ ->

                val position = app.devices.findAll().indexOf(device)
                if (position != -1) {
                    app.devices.delete(device)

                    val adapter = binding.recyclerView.adapter
                    if (app.devices.findAll().isEmpty()) {
                        adapter?.notifyDataSetChanged()
                    } else {
                        adapter?.notifyItemRemoved(position)
                    }
                }
            }
            .setNegativeButton("No", null)
            .show()
    }


    private val mapIntentLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        )    { }

    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                (binding.recyclerView.adapter)?.notifyItemRangeChanged(
                    0,
                    app.devices.findAll().size
                )
            }
        }

    override fun onDeviceClick(device: DeviceModel) {
        val launcherIntent = Intent(this, DeviceView::class.java)
        launcherIntent.putExtra("device_edit", device)
        getClickResult.launch(launcherIntent)
    }

    private val getClickResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                (binding.recyclerView.adapter)?.notifyItemRangeChanged(
                    0,
                    app.devices.findAll().size
                )
            }
        }
}

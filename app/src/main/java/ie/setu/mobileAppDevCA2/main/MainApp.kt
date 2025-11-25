package ie.setu.mobileAppDevCA2.main

import android.app.Application
import com.github.ajalt.timberkt.Timber
import ie.setu.mobileAppDevCA2.models.DeviceMemStore
import timber.log.Timber.i

class MainApp : Application() {

    lateinit var devices: DeviceMemStore

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        i("Device app started")

        devices = DeviceMemStore(this)
        //devices.load()
    }
}
package com.sibilantsolutions.indri.android

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import org.fourthline.cling.android.AndroidUpnpService
import org.fourthline.cling.android.AndroidUpnpServiceImpl
import org.fourthline.cling.android.FixedAndroidLogHandler
import org.fourthline.cling.model.meta.Device
import org.fourthline.cling.model.meta.LocalDevice
import org.fourthline.cling.model.meta.RemoteDevice
import org.fourthline.cling.model.meta.Service
import org.fourthline.cling.registry.Registry
import org.fourthline.cling.registry.RegistryListener
import java.lang.Exception


class MainActivity : AppCompatActivity() {

//    private val registryListener = BrowseRegistryListener()
    private val registryListener = MyLocalListener()

    class MyLocalListener : RegistryListener {
        val TAG = "MyLocalListener"

        override fun localDeviceRemoved(registry: Registry?, device: LocalDevice?) {
            Log.i(TAG, "localDeviceRemoved")
        }

        override fun remoteDeviceDiscoveryStarted(registry: Registry?, device: RemoteDevice?) {
            Log.i(TAG, "remoteDeviceDiscoveryStarted: " + device?.details?.friendlyName)
        }

        override fun remoteDeviceDiscoveryFailed(registry: Registry?, device: RemoteDevice?, ex: Exception?) {
            Log.i(TAG, "remoteDeviceDiscoveryFailed")
        }

        override fun afterShutdown() {
            Log.i(TAG, "afterShutdown")
        }

        override fun remoteDeviceAdded(registry: Registry?, device: RemoteDevice?) {
            deviceAdded(device)
        }

        override fun remoteDeviceUpdated(registry: Registry?, device: RemoteDevice?) {
            Log.i(TAG, "remoteDeviceUpdated")
        }

        override fun beforeShutdown(registry: Registry?) {
            Log.i(TAG, "beforeShutdown")
        }

        override fun remoteDeviceRemoved(registry: Registry?, device: RemoteDevice?) {
            Log.i(TAG, "remoteDeviceRemoved")
        }

        override fun localDeviceAdded(registry: Registry?, device: LocalDevice?) {
            deviceAdded(device)
        }

        fun deviceAdded(device: Device<*, out Device<*, *, *>, out Service<*, *>>?) {
            Log.i(TAG, "deviceAdded: " + device?.details?.friendlyName)
        }

    }

    private var upnpService: AndroidUpnpService? = null

    private val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {

            Log.i("jttest", "Connected service.")

            upnpService = service as AndroidUpnpService

//            // Clear the list
//            listAdapter.clear()

            // Get ready for future device advertisements
            upnpService!!.registry.addListener(registryListener)

            // Now add all devices to the list we already know about
            for (device in upnpService!!.registry.devices) {
                registryListener.deviceAdded(device)
            }

            // Search asynchronously for all devices, they will respond soon
            upnpService!!.controlPoint.search()
        }

        override fun onServiceDisconnected(className: ComponentName) {
            upnpService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }


        // Fix the logging integration between java.util.logging and Android internal logging
        org.seamless.util.logging.LoggingUtil.resetRootHandler(
                FixedAndroidLogHandler()
        )

        // This will start the UPnP service if it wasn't already started
        applicationContext.bindService(
                Intent(this, AndroidUpnpServiceImpl::class.java),
                serviceConnection,
                Context.BIND_AUTO_CREATE
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (upnpService != null) {
            upnpService!!.getRegistry().removeListener(registryListener)
        }
        // This will stop the UPnP service if nobody else is bound to it
        applicationContext.unbindService(serviceConnection)
    }

}

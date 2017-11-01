package com.sibilantsolutions.indri.android

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import org.fourthline.cling.android.AndroidUpnpService
import org.fourthline.cling.model.meta.Device
import org.fourthline.cling.model.meta.LocalDevice
import org.fourthline.cling.model.meta.RemoteDevice
import org.fourthline.cling.model.meta.Service
import org.fourthline.cling.registry.Registry
import org.fourthline.cling.registry.RegistryListener
import java.lang.Exception

/**
 * Created by jt on 10/29/17.
 */
class SearchPresenter : SearchContract.Presenter {

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

    override fun sc(): ServiceConnection = serviceConnection

    override fun onDestroy() {
        if (upnpService != null) {
            upnpService!!.getRegistry().removeListener(registryListener)
        }
    }
}

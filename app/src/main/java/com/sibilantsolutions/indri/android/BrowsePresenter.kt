package com.sibilantsolutions.indri.android

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import org.fourthline.cling.android.AndroidUpnpService
import org.fourthline.cling.model.ServiceReference
import org.fourthline.cling.model.types.UDAServiceType
import java.util.concurrent.TimeUnit

/**
 * Created by jt on 11/11/17.
 */
class BrowsePresenter(private val browseContractView: BrowseContract.View) : BrowseContract.Presenter {

    private var androidUpnpService: AndroidUpnpService? = null

    private val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {

            androidUpnpService = service as AndroidUpnpService
        }

        override fun onServiceDisconnected(className: ComponentName) {
            androidUpnpService = null
        }
    }

    override fun sc() = serviceConnection

    override fun setContent(serializableDIDLContent: SerializableDIDLContent, serviceReference: ServiceReference) {
        browseContractView.setContent(serializableDIDLContent, serviceReference)
    }

    override fun browse(containerId: String, serviceReference: ServiceReference) {
        val upnpService = androidUpnpService?.get()
        if (upnpService != null) {
            val service = upnpService.registry.getService(serviceReference)
            com.sibilantsolutions.indri.android.browse(service, containerId, upnpService)
                    .map { SerializableDIDLContent.mapToSerializable(it.didl) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { browseContractView.setContent(it, serviceReference) },
                            { Log.e("cling", "Browse problem:", it) }
                    )
        }
    }

    override fun play(resValue: String) {
        Log.i("cling", "Want to play $resValue.")
        val upnpService = androidUpnpService?.get()
        if (upnpService != null) {
            val avTransportType = UDAServiceType("AVTransport")

            val devices = upnpService.registry.getDevices(avTransportType)
            Log.i("cling", "Devices=$devices")
            val avService = devices.firstOrNull()?.findService(avTransportType)
            if (avService != null) {
                setUri(avService, resValue, upnpService)
                        .timeout(7, TimeUnit.SECONDS)
                        .subscribe(
                                { Log.i("cling", "setUri!")
                                    com.sibilantsolutions.indri.android.play(avService, upnpService)
                                            .timeout(2, TimeUnit.SECONDS)
                                            .subscribe(
                                                    { Log.i("cling", "play!") },
                                                    { Log.e("cling", "Trouble with play:", it) })
                                },
                                { Log.e("cling", "Trouble with seturi:", it) }
                        )
            } else {
                Log.e("cling", "No viable devices.")
            }
        } else {
            Log.i("cling", "Service is gone.")
        }
    }

}
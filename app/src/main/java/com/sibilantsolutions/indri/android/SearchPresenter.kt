package com.sibilantsolutions.indri.android

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import org.fourthline.cling.android.AndroidUpnpService
import org.fourthline.cling.model.meta.Device
import org.fourthline.cling.model.types.UDAServiceType

/**
 * Created by jt on 10/29/17.
 */
class SearchPresenter constructor(private val searchContractView: SearchContract.View) : SearchContract.Presenter {

    private var androidUpnpService: AndroidUpnpService? = null

    private var registryListenerDisposable: Disposable? = null

    private val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {

            val localUpnpService = service as AndroidUpnpService
            androidUpnpService = localUpnpService

            // Now add all devices to the list we already know about
            for (device in localUpnpService.registry.devices) {
                searchContractView.addDevice(device)
            }

            val registryListener = RxClingRegistryListener(localUpnpService.get()).registryListener()

            registryListenerDisposable = registryListener
                    .filter { clingEvent ->
                        clingEvent.clingEventType == ClingEventType.remoteDeviceAdded
                                || clingEvent.clingEventType == ClingEventType.localDeviceAdded }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ clingEvent -> searchContractView.addDevice(clingEvent.device) })
        }

        override fun onServiceDisconnected(className: ComponentName) {
            registryListenerDisposable?.dispose()
            registryListenerDisposable = null
            androidUpnpService = null
        }
    }

    override fun sc(): ServiceConnection = serviceConnection

    override fun search() {
        searchContractView.snackbar("Searching network...")
        androidUpnpService?.controlPoint!!.search()
    }

    override fun onDestroy() {
        registryListenerDisposable?.dispose()
        registryListenerDisposable = null
    }

    override fun browse(device: Device<*, *, *>) {
        val upnpService = androidUpnpService?.get()

        if (upnpService != null) {
            browse(device.findService(UDAServiceType("ContentDirectory")), "0", upnpService)
                    .map { it.didl }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { didl -> searchContractView.show(didl) },
                            { t ->
                                searchContractView.snackbar("Problem browsing")
                                Log.e("cling", "Trouble browsing:", t)
                            })
        }
    }

}

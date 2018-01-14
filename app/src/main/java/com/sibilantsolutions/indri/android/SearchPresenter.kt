package com.sibilantsolutions.indri.android

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.sibilantsolutions.indri.domain.usecase.cling.ClingBrowseImpl
import com.sibilantsolutions.indri.domain.usecase.cling.ClingRegistryListenerImpl
import com.sibilantsolutions.indri.domain.usecase.cling.ClingRegistryListenerImpl.ClingRegistryEventType.localDeviceAdded
import com.sibilantsolutions.indri.domain.usecase.cling.ClingRegistryListenerImpl.ClingRegistryEventType.remoteDeviceAdded
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

            val registryListener = ClingRegistryListenerImpl(localUpnpService.get().registry).registryListener()

            registryListenerDisposable = registryListener
                    .filter { clingEvent ->
                        when (clingEvent.clingRegistryEventType) {
                            remoteDeviceAdded, localDeviceAdded -> true
                            else -> false
                        }
                    }
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
        //This invokes an async search operation, the results of which will be observable in the
        // registry: known devices can be iterated and new devices are fired to a listener.
        androidUpnpService?.controlPoint!!.search()
    }

    override fun onDestroy() {
        registryListenerDisposable?.dispose()
        registryListenerDisposable = null
    }

    override fun browse(device: Device<*, *, *>) {
        val upnpService = androidUpnpService?.get()

        if (upnpService != null) {
            val service = device.findService(UDAServiceType("ContentDirectory"))
            ClingBrowseImpl(service, upnpService.controlPoint).browse("0")
                    .map { it.didl }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { didl -> searchContractView.show(didl, service) },
                            { t ->
                                searchContractView.snackbar("Problem browsing")
                                Log.e("cling", "Trouble browsing:", t)
                            })
        }
    }

}

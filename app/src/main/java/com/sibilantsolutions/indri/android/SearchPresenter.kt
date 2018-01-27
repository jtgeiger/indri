package com.sibilantsolutions.indri.android

import android.util.Log
import android.view.View
import com.sibilantsolutions.indri.domain.usecase.cling.ClingRegistryListener.ClingRegistryEventType.*
import com.sibilantsolutions.indri.domain.usecase.cling.ClingRegistryListenerImpl
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.fourthline.cling.android.AndroidUpnpService
import org.fourthline.cling.model.meta.Device
import org.fourthline.cling.model.meta.DeviceIdentity
import org.fourthline.cling.model.meta.Service
import org.fourthline.cling.model.types.UDAServiceType

/**
 * Created by jt on 10/29/17.
 */
class SearchPresenter (private val searchContractView: SearchContract.View,
                       serviceObservable: Observable<AndroidUpnpService>) : SearchContract.Presenter {

    private val compositeDisposable = CompositeDisposable()

    init {
        compositeDisposable.add(
                serviceObservable.subscribe(
                        {
                            androidUpnpService = it
                            //TODO: Enable Search FAB
                            populateKnownDevices(it)
                            compositeDisposable.add(listenForDeviceChanges(it))
                        },
                        {
                            searchContractView.snackbar("Error with UPnP service")
                            Log.e("indri", "Error with UPnP service", it)
                        },
                        {
                            androidUpnpService = null
                            //TODO: Disable Search FAB
                        }
                ))
    }

    private var androidUpnpService: AndroidUpnpService? = null

    override fun search() {
        searchContractView.snackbar("Searching network...")
        //This invokes an async search operation, the results of which will be observable in the
        // registry: known devices can be iterated and new devices are fired to a listener.
        androidUpnpService?.controlPoint!!.search()
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
    }

    private fun browse(service: Service<*, *>) {
        val upnpService = androidUpnpService?.get()

        if (upnpService != null) {
            searchContractView.show("0", service.reference.toString())
        }
    }

    private fun listenForDeviceChanges(androidUpnpService: AndroidUpnpService) : Disposable {
        val registryListener = ClingRegistryListenerImpl(androidUpnpService.registry).registryListener()

        return registryListener
                .filter { clingEvent ->
                    when (clingEvent.clingRegistryEventType) {
                        remoteDeviceAdded, localDeviceAdded -> true
                        remoteDeviceRemoved, localDeviceRemoved -> true
                        else -> false
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            populateKnownDevices(androidUpnpService)
                        },
                        {
                            searchContractView.snackbar("Error listening for device changes")
                            Log.e("indri", "Error listening for device changes:", it)
                        }
                )
    }

    private fun populateKnownDevices(androidUpnpService: AndroidUpnpService) {
        // Now add all devices to the list we already know about
        val list: List<SearchViewModel.Entry> = mapAllKnownDevicesToEntries(androidUpnpService)

        searchContractView.render(SearchViewModel(list))
    }

    private fun mapAllKnownDevicesToEntries(upnpService: AndroidUpnpService): List<SearchViewModel.Entry> {
        return upnpService.registry.devices
                .map { device -> deviceToEntry(device) }
    }

    private fun deviceToEntry(device: Device<DeviceIdentity, Device<*, *, *>, Service<*, *>>): SearchViewModel.Entry {
        return SearchViewModel.Entry(device.details.friendlyName,
                View.OnClickListener {
                    val s = device.findService(UDAServiceType("ContentDirectory"))
                    if (s != null) {
                        browse(s)
                    }
                })
    }

}

package com.sibilantsolutions.indri.android

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.sibilantsolutions.indri.domain.usecase.cling.ClingBrowseImpl
import com.sibilantsolutions.indri.domain.usecase.cling.ClingPlayImpl
import com.sibilantsolutions.indri.domain.usecase.cling.ClingSetUriImpl
import com.sibilantsolutions.indri.domain.usecase.cling.ClingSpider
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.fourthline.cling.android.AndroidUpnpService
import org.fourthline.cling.model.ServiceReference
import org.fourthline.cling.model.types.UDAServiceType
import org.fourthline.cling.support.model.DIDLContent
import org.fourthline.cling.support.model.container.StorageFolder
import org.fourthline.cling.support.model.item.MusicTrack
import java.util.concurrent.TimeUnit

/**
 * Created by jt on 11/11/17.
 */
class BrowsePresenter(private val browseContractView: BrowseContract.View) : BrowseContract.Presenter {

    private var androidUpnpService: AndroidUpnpService? = null

    private lateinit var containerId: String
    private lateinit var serviceId: String

    private val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {

            androidUpnpService = service as AndroidUpnpService
        }

        override fun onServiceDisconnected(className: ComponentName) {
            androidUpnpService = null
        }
    }

    init {
        //TODO: Dispose on lifecycle events.
        browseContractView.browseObservable()
                .subscribe({ browse(it, this.serviceId)})
    }

    override fun sc() = serviceConnection

    override fun browse(containerId: String, serviceId: String) {
        this.containerId = containerId
        this.serviceId = serviceId

        val upnpService = androidUpnpService?.get() ?: return
        val service = upnpService.registry.getService(ServiceReference(serviceId))
        ClingBrowseImpl(service, upnpService.controlPoint).browse(containerId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            browseContractView.render(BrowseViewModel(
                                    it.didl.containers
                                            .filterIsInstance(StorageFolder::class.java)
                                            .map { BrowseViewModel.Container(it.id, it.parentID, it.title) },
                                    it.didl.items
                                            .filterIsInstance(MusicTrack::class.java)
                                            .map { BrowseViewModel.Item(it.id, it.parentID, it.title, it.creator.orEmpty(), it.resources.first().value, it.resources.first().duration) }))
                        },
                        { Log.e("cling", "Browse problem:", it) }
                )
    }

    /**
     * @param resValue String resource name of the media to play, like an HTML anchor target URL.
     * This is given to the renderer who is then responsible for fetching it over the network and
     * starting playback.
     */
    override fun play(resValue: String) {
        Log.i("cling", "Want to play $resValue.")
        val upnpService = androidUpnpService?.get()
        if (upnpService != null) {
            val avTransportType = UDAServiceType("AVTransport")

            val devices = upnpService.registry.getDevices(avTransportType)
            Log.i("cling", "Devices=$devices")

            //1/11/18: This is sketchy -- picking any arbitrary device with an AVTransport service.
            //The device here is the renderer, i.e. a playback device like a receiver.  We should
            // allow this to be selected by the user in case there are multiple.
            val avService = devices.firstOrNull()?.findService(avTransportType)

            if (avService != null) {
                ClingSetUriImpl(avService, upnpService.controlPoint).setUri(resValue)
                        .timeout(7, TimeUnit.SECONDS)
                        .subscribe(
                                { Log.i("cling", "setUri!")
                                    ClingPlayImpl(avService, upnpService.controlPoint).play()
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

    override fun spider() {
        val upnpService = androidUpnpService?.get() ?: return
        val service = upnpService.registry.getService(ServiceReference(serviceId))
        val spider: Flowable<DIDLContent> = ClingSpider(ClingBrowseImpl(service, upnpService.controlPoint))
                .spider(this.containerId)
        spider
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.i("didl", "Folder count=${it.containers.size}, item count=${it.items.size}")
                }, {
                    browseContractView.snackbar("Error: $it")
                }, {
                    browseContractView.snackbar("Finished!")
                })

    }

}
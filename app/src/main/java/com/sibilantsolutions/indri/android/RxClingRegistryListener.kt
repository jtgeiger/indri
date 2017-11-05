package com.sibilantsolutions.indri.android

import android.util.Log
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import org.fourthline.cling.UpnpService
import org.fourthline.cling.model.meta.Device
import org.fourthline.cling.model.meta.LocalDevice
import org.fourthline.cling.model.meta.RemoteDevice
import org.fourthline.cling.registry.Registry
import org.fourthline.cling.registry.RegistryListener
import java.lang.Exception

/**
 * Created by jt on 11/2/17.
 */
class RxClingRegistryListener(val upnpService: UpnpService) {


    fun registryListener(): Flowable<ClingEvent> {
        return Flowable.create(
                { emitter: FlowableEmitter<ClingEvent> ->
                    val registryListener = object: RegistryListener {
                        override fun localDeviceRemoved(registry: Registry?, device: LocalDevice?) {
                            emitter.onNext(ClingEvent(ClingEventType.localDeviceRemoved, registry!!, device!!))
                        }

                        override fun remoteDeviceDiscoveryStarted(registry: Registry?, device: RemoteDevice?) {
                            emitter.onNext(ClingEvent(ClingEventType.remoteDeviceDiscoveryStarted, registry!!, device!!))
                        }

                        override fun remoteDeviceDiscoveryFailed(registry: Registry?, device: RemoteDevice?, ex: Exception?) {
                            emitter.onError(ex!!)
                        }

                        override fun afterShutdown() {
                            emitter.onComplete()
                        }

                        override fun remoteDeviceAdded(registry: Registry?, device: RemoteDevice?) {
                            emitter.onNext(ClingEvent(ClingEventType.remoteDeviceAdded, registry!!, device!!))
                        }

                        override fun remoteDeviceUpdated(registry: Registry?, device: RemoteDevice?) {
                            emitter.onNext(ClingEvent(ClingEventType.remoteDeviceUpdated, registry!!, device!!))
                        }

                        override fun beforeShutdown(registry: Registry?) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                        override fun remoteDeviceRemoved(registry: Registry?, device: RemoteDevice?) {
                            emitter.onNext(ClingEvent(ClingEventType.remoteDeviceRemoved, registry!!, device!!))
                        }

                        override fun localDeviceAdded(registry: Registry?, device: LocalDevice?) {
                            emitter.onNext(ClingEvent(ClingEventType.localDeviceAdded, registry!!, device!!))
                        }

                    }

                    emitter.setCancellable({
                        Log.i("cling", "The emitter has been cancelled.")
                        upnpService.registry.removeListener(registryListener) })

                    upnpService.registry.addListener(registryListener)
                },
                BackpressureStrategy.BUFFER
        )
                //Cling callback methods can fire on any thread so we need to serialize.
                .serialize()
    }

}

class ClingEvent(val clingEventType: ClingEventType, val registry: Registry, val device: Device<*, *, *>)

enum class ClingEventType {
    remoteDeviceDiscoveryStarted,
    //remoteDeviceDiscoveryFailed,
    remoteDeviceAdded,
    remoteDeviceUpdated,
    remoteDeviceRemoved,
    localDeviceAdded,
    localDeviceRemoved,
    beforeShutdown,
    //afterShutdown,
    ;
}

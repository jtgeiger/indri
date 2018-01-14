package com.sibilantsolutions.indri.domain.usecase.cling

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import org.fourthline.cling.model.meta.Device
import org.fourthline.cling.model.meta.LocalDevice
import org.fourthline.cling.model.meta.RemoteDevice
import org.fourthline.cling.registry.Registry
import org.fourthline.cling.registry.RegistryListener
import java.lang.Exception

/**
 * Created by jt on 11/2/17.
 */
class ClingRegistryListenerImpl(private val registry: Registry) {

    fun registryListener(): Flowable<ClingRegistryEvent> {
        return Flowable.create(
                { emitter: FlowableEmitter<ClingRegistryEvent> ->
                    val registryListener = object: RegistryListener {
                        override fun localDeviceRemoved(registry: Registry?, device: LocalDevice?) {
                            emitter.onNext(ClingRegistryEvent(ClingRegistryEventType.localDeviceRemoved, registry!!, device!!))
                        }

                        override fun remoteDeviceDiscoveryStarted(registry: Registry?, device: RemoteDevice?) {
                            emitter.onNext(ClingRegistryEvent(ClingRegistryEventType.remoteDeviceDiscoveryStarted, registry!!, device!!))
                        }

                        override fun remoteDeviceDiscoveryFailed(registry: Registry?, device: RemoteDevice?, ex: Exception?) {
                            emitter.onError(ex!!)
                        }

                        override fun afterShutdown() {
                            emitter.onComplete()
                        }

                        override fun remoteDeviceAdded(registry: Registry?, device: RemoteDevice?) {
                            emitter.onNext(ClingRegistryEvent(ClingRegistryEventType.remoteDeviceAdded, registry!!, device!!))
                        }

                        override fun remoteDeviceUpdated(registry: Registry?, device: RemoteDevice?) {
                            emitter.onNext(ClingRegistryEvent(ClingRegistryEventType.remoteDeviceUpdated, registry!!, device!!))
                        }

                        override fun beforeShutdown(registry: Registry?) {
                            //No-op.
                        }

                        override fun remoteDeviceRemoved(registry: Registry?, device: RemoteDevice?) {
                            emitter.onNext(ClingRegistryEvent(ClingRegistryEventType.remoteDeviceRemoved, registry!!, device!!))
                        }

                        override fun localDeviceAdded(registry: Registry?, device: LocalDevice?) {
                            emitter.onNext(ClingRegistryEvent(ClingRegistryEventType.localDeviceAdded, registry!!, device!!))
                        }

                    }

                    emitter.setCancellable {
                        registry.removeListener(registryListener)
                    }

                    registry.addListener(registryListener)
                },
                BackpressureStrategy.BUFFER
        )
                //Cling callback methods can fire on any thread so we need to serialize.
                .serialize()
    }

    data class ClingRegistryEvent(
            val clingRegistryEventType: ClingRegistryEventType,
            val registry: Registry,
            val device: Device<*, *, *>)

    enum class ClingRegistryEventType {
        remoteDeviceDiscoveryStarted,
        //remoteDeviceDiscoveryFailed,
        remoteDeviceAdded,
        remoteDeviceUpdated,
        remoteDeviceRemoved,
        localDeviceAdded,
        localDeviceRemoved,
//    beforeShutdown,
        //afterShutdown,
        ;
    }

}

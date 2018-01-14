package com.sibilantsolutions.indri.domain.usecase.cling

import io.reactivex.Flowable
import org.fourthline.cling.model.meta.Device
import org.fourthline.cling.registry.Registry

/**
 * Created by jt on 1/14/18.
 */
interface ClingRegistryListener {
    fun registryListener(): Flowable<ClingRegistryEvent>

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
package com.sibilantsolutions.indri.domain.repository

import io.reactivex.Flowable
import io.reactivex.Maybe
import org.fourthline.cling.model.meta.Device
import org.fourthline.cling.model.types.UDN
import org.fourthline.cling.registry.Registry

/**
* Created by jt on 1/11/18.
*/
class DeviceRepositoryImpl(private val registry: Registry) : DeviceRepository {

    /**
     * @param id String that will be treated as a UDN.
     * @return Maybe<Device> that will have the Device if it could be found by UDN or else empty.
     */
    override fun findDevice(id: String): Maybe<Device<*, *, *>> {
        return Maybe.fromCallable {
            val udn = UDN.valueOf(id)
            return@fromCallable registry.getDevice(udn, true)
        }
    }

    /**
     * Return all devices currently known to the registry.  This is a cold Flowable.
     *
     * Devices become known to the registry when they announce their presence on the network, which
     * may happen naturally but can also be solicited by broadcasting a search request.
     *
     * @return Cold Flowable of known devices.
     */
    override fun devices() : Flowable<Device<*, *, *>> {
        return Flowable.fromIterable(registry.devices)
    }

}
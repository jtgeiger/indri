package com.sibilantsolutions.indri.domain.repository

import io.reactivex.Flowable

/**
 * Created by jt on 1/11/18.
 */
interface DeviceRepository {

    fun devices() : Flowable<IndriDeviceEvent>

    data class IndriDevice(val deviceUdn: String, val friendlyName: String, val contentDirectoryServiceReference: String)

    data class IndriDeviceEvent(val indriDevice: IndriDevice, val eventType: EventType)

    enum class EventType {
        //TODO: Support a CLEAR_ALL event, in case the service disconnected and reconnected.
        ADDED, REMOVED
    }

}

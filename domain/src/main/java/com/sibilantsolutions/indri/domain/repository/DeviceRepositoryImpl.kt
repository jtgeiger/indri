package com.sibilantsolutions.indri.domain.repository

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.ReplaySubject

/**
* Created by jt on 1/11/18.
*/
class DeviceRepositoryImpl() : MutableDeviceRepository {

    private val subject = ReplaySubject
            .create<DeviceRepository.IndriDeviceEvent>()
            //Make sure that this subject obeys the threading rules; Cling callbacks can happen on
            //any thread so we want to be sure to that the subject's emissions are well-behaved.
            .toSerialized()

    override fun fire(indriDeviceEvent: DeviceRepository.IndriDeviceEvent) {
        subject.onNext(indriDeviceEvent)
    }

    /**
     * Return all devices currently known to the registry.  This is backed by a ReplaySubject so
     * late subscribers will see all events.
     *
     * Devices become known to the registry when they announce their presence on the network, which
     * may happen naturally but can also be solicited by broadcasting a search request.
     *
     * @return Flowable of known devices.
     */
    override fun devices() : Flowable<DeviceRepository.IndriDeviceEvent> {
        return subject.toFlowable(BackpressureStrategy.BUFFER)
    }

}

package com.sibilantsolutions.indri.domain.repository

/**
 * Created by jt on 1/28/18.
 */
interface MutableDeviceRepository : DeviceRepository {

    fun fire(indriDeviceEvent: DeviceRepository.IndriDeviceEvent)

}

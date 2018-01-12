package com.sibilantsolutions.indri.domain.repository

import io.reactivex.Flowable
import io.reactivex.Maybe
import org.fourthline.cling.model.meta.Device

/**
 * Created by jt on 1/11/18.
 */
interface DeviceRepository {

    fun devices() : Flowable<Device<*, *, *>>

    fun findDevice(id: String) : Maybe<Device<*, *, *>>
}
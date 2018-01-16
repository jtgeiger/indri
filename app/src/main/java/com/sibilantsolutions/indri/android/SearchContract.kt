package com.sibilantsolutions.indri.android

import android.content.ServiceConnection
import org.fourthline.cling.model.meta.Device
import org.fourthline.cling.model.meta.Service
import org.fourthline.cling.support.model.DIDLContent

/**
 * Created by jt on 10/29/17.
 */
interface SearchContract {

    interface View {
        fun addDevice(device: Device<*, *, *>)
        fun snackbar(msg: String)
        fun show(containerId: String, didl: DIDLContent, service: Service<*, *>)
    }

    interface Presenter {
        fun sc(): ServiceConnection
        fun onDestroy()
        fun search()
        fun browse(device: Device<*, *, *>)
    }

}

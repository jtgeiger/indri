package com.sibilantsolutions.indri.android

import android.content.ServiceConnection
import org.fourthline.cling.model.meta.Device

/**
 * Created by jt on 10/29/17.
 */
interface SearchContract {

    interface View {
        fun addDevice(device: Device<*, *, *>)
        fun snackbar(msg: String)
    }

    interface Presenter {
        fun sc(): ServiceConnection
        fun onDestroy()
        fun search()
    }

}

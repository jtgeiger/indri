package com.sibilantsolutions.indri.android

import android.content.ServiceConnection

/**
 * Created by jt on 10/29/17.
 */
interface SearchContract {

    interface View {
        fun render(searchViewModel: SearchViewModel)
        fun snackbar(msg: String)
        fun show(containerId: String, serviceId: String)
    }

    interface Presenter {
        fun sc(): ServiceConnection
        fun onDestroy()
        fun search()
    }

}

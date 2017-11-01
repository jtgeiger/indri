package com.sibilantsolutions.indri.android

import android.content.ServiceConnection

/**
 * Created by jt on 10/29/17.
 */
interface SearchContract {

    interface View {

    }

    interface Presenter {
        fun sc(): ServiceConnection
        fun onDestroy()
    }

}

package com.sibilantsolutions.indri.android

import io.reactivex.Observable

/**
 * Created by jt on 10/29/17.
 */
interface BrowseContract {

    interface View {
        fun render(browseViewModel: BrowseViewModel)

        fun browseObservable(): Observable<String>

        fun snackbar(msg: String)

    }

    interface Presenter {

        fun browse(containerId: String, serviceId: String)

        fun play(resValue: String)

        fun spider()

        fun onDestroy()
    }

}
package com.sibilantsolutions.indri.android

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
        fun onDestroy()
        fun search()
    }

}

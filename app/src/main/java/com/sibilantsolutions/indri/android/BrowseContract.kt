package com.sibilantsolutions.indri.android

import android.content.ServiceConnection
import io.reactivex.Observable
import org.fourthline.cling.model.ServiceReference

/**
 * Created by jt on 10/29/17.
 */
interface BrowseContract {

    interface View {
        fun setContent(serializableDIDLContent: SerializableDIDLContent, serviceReference: ServiceReference)

        fun browseObservable(): Observable<Pair<String, ServiceReference>>

        fun snackbar(msg: String)

    }

    interface Presenter {
        fun sc(): ServiceConnection

        //TODO HACK: This data should get fetched from a repo.
        fun setContent(serializableDIDLContent: SerializableDIDLContent, serviceReference: ServiceReference)

        fun play(resValue: String)

        fun spider(serviceReference: ServiceReference)
    }

}
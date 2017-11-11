package com.sibilantsolutions.indri.android

import android.content.ServiceConnection
import org.fourthline.cling.model.ServiceReference

/**
 * Created by jt on 10/29/17.
 */
interface BrowseContract {

    interface View {
        fun setContent(serializableDIDLContent: SerializableDIDLContent, serviceReference: ServiceReference)
    }

    interface Presenter {
        fun sc(): ServiceConnection

        //TODO HACK: This data should get fetched from a repo.
        fun setContent(serializableDIDLContent: SerializableDIDLContent, serviceReference: ServiceReference)

        fun browse(containerId: String, serviceReference: ServiceReference)
    }

}
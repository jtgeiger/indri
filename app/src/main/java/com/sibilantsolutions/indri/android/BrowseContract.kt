package com.sibilantsolutions.indri.android

import android.content.ServiceConnection

/**
 * Created by jt on 10/29/17.
 */
interface BrowseContract {

    interface View {
        fun setContent(containerTitles: List<String>, itemTitles: List<String>)
    }

    interface Presenter {
        fun sc(): ServiceConnection

        //TODO HACK: This data should get fetched from a repo.
        fun setContent(containerTitles: List<String>, itemTitles: List<String>)

        fun browse(containerId: String)
    }

}
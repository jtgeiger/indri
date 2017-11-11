package com.sibilantsolutions.indri.android

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import org.fourthline.cling.android.AndroidUpnpService

/**
 * Created by jt on 11/11/17.
 */
class BrowsePresenter(private val browseContractView: BrowseContract.View) : BrowseContract.Presenter {

    private var androidUpnpService: AndroidUpnpService? = null

    private val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {

            androidUpnpService = service as AndroidUpnpService
        }

        override fun onServiceDisconnected(className: ComponentName) {
            androidUpnpService = null
        }
    }

    override fun sc() = serviceConnection

    override fun setContent(containerTitles: List<String>, itemTitles: List<String>) {
        browseContractView.setContent(containerTitles, itemTitles)
    }

    override fun browse(containerId: String) {
        Log.i("cling", "Request to browse cont id=$containerId")
    }

}
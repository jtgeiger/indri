package com.sibilantsolutions.indri.devicelibrary

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import io.reactivex.Observable
import org.fourthline.cling.android.AndroidUpnpService

/**
 * Created by jt on 1/27/18.
 */
class ObservableServiceBinder {

    fun bindService(context : Context) : Observable<AndroidUpnpService> {

        return Observable.create { emitter ->

            val serviceConnection = object : ServiceConnection {

                override fun onServiceDisconnected(name: ComponentName?) {
                    emitter.onComplete()
                }

                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                    when (service) {
                        is AndroidUpnpService -> emitter.onNext(service)
                        else -> emitter.onError(
                                ClassCastException("Expected service of type=${AndroidUpnpService::class}"))
                    }
                }

            }

            // This will start the UPnP service if it wasn't already started
            val success = context.bindService(clingAndroidUpnpServiceIntent(context),
                    serviceConnection, Context.BIND_AUTO_CREATE)

            if (success) {
                // This will stop the UPnP service if nobody else is bound to it
                emitter.setCancellable { context.unbindService(serviceConnection) }
            } else {
                emitter.onError(RuntimeException("Failed to bind service"))
            }

        }
    }
}

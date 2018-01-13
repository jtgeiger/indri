package com.sibilantsolutions.indri.domain.usecase.cling

import io.reactivex.Completable
import org.fourthline.cling.controlpoint.ControlPoint
import org.fourthline.cling.model.action.ActionInvocation
import org.fourthline.cling.model.message.UpnpResponse
import org.fourthline.cling.model.meta.Service
import org.fourthline.cling.support.avtransport.callback.SetAVTransportURI

/**
 * Created by jt on 1/13/18.
 */
class ClingSetUriImpl(private val service: Service<*, *>, private val controlPoint: ControlPoint) {

    fun setUri(uri: String): Completable {

        return Completable.create { emitter ->
            val setAVTransportURICallback = object : SetAVTransportURI(service, uri) {

                override fun success(invocation: ActionInvocation<*>?) {
                    super.success(invocation)
                    emitter.onComplete()
                }

                override fun failure(invocation: ActionInvocation<*>, operation: UpnpResponse, defaultMsg: String) {
                    emitter.onError(RuntimeException("setUri failed; operation=$operation, defaultMsg=$defaultMsg"))
                }
            }

            val future = controlPoint.execute(setAVTransportURICallback)

            emitter.setCancellable { future.cancel(false) }
        }
    }

}

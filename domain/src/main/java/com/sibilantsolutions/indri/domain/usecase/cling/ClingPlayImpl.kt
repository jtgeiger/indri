package com.sibilantsolutions.indri.domain.usecase.cling

import io.reactivex.Completable
import org.fourthline.cling.controlpoint.ControlPoint
import org.fourthline.cling.model.action.ActionInvocation
import org.fourthline.cling.model.message.UpnpResponse
import org.fourthline.cling.model.meta.Service
import org.fourthline.cling.support.avtransport.callback.Play

/**
 * Created by jt on 1/13/18.
 */
class ClingPlayImpl(private val service: Service<*, *>, private val controlPoint: ControlPoint) {

    fun play(): Completable {
        return Completable.create { emitter ->
            val playCallback = object : Play(service) {
                override fun success(invocation: ActionInvocation<*>?) {
                    super.success(invocation)
                    emitter.onComplete()
                }

                override fun failure(invocation: ActionInvocation<*>, operation: UpnpResponse, defaultMsg: String) {
                    emitter.onError(RuntimeException("play failed; operation=$operation, defaultMsg=$defaultMsg"))
                }
            }

            val future = controlPoint.execute(playCallback)

            emitter.setCancellable { future.cancel(false) }

        }
    }
}
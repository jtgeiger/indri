package com.sibilantsolutions.indri.domain.usecase.cling

import com.sibilantsolutions.indri.domain.usecase.cling.ClingBrowse.BrowseResult
import io.reactivex.Single
import org.fourthline.cling.controlpoint.ControlPoint
import org.fourthline.cling.model.action.ActionInvocation
import org.fourthline.cling.model.message.UpnpResponse
import org.fourthline.cling.model.meta.Service
import org.fourthline.cling.support.contentdirectory.callback.Browse
import org.fourthline.cling.support.model.BrowseFlag
import org.fourthline.cling.support.model.DIDLContent

/**
 * Created by jt on 1/13/18.
 */
class ClingBrowseImpl(private val service: Service<*, *>, private val controlPoint: ControlPoint) : ClingBrowse {

    override fun browse(containerId: String): Single<BrowseResult> {
        return Single.create { emitter ->
            val browseCallback = object : Browse(service, containerId, BrowseFlag.DIRECT_CHILDREN) {
                override fun updateStatus(ignored: Status?) {
                    //No-op.
                }

                override fun received(actionInvocation: ActionInvocation<out Service<*, *>>?, didl: DIDLContent?) {
                    emitter.onSuccess(BrowseResult(actionInvocation!!, didl!!))
                }

                override fun failure(invocation: ActionInvocation<out Service<*, *>>?, operation: UpnpResponse?, defaultMsg: String?) {
                    emitter.onError(RuntimeException("Browse failed; operation=$operation, defaultMsg=$defaultMsg"))
                }

            }

            val future = controlPoint.execute(browseCallback)

            emitter.setCancellable { future.cancel(false) }
        }
    }

}

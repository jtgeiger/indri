package com.sibilantsolutions.indri.domain.usecase.cling

import com.sibilantsolutions.indri.domain.model.*
import io.reactivex.Single
import org.fourthline.cling.controlpoint.ControlPoint
import org.fourthline.cling.model.action.ActionInvocation
import org.fourthline.cling.model.message.UpnpResponse
import org.fourthline.cling.model.meta.Service
import org.fourthline.cling.support.contentdirectory.callback.Browse
import org.fourthline.cling.support.model.BrowseFlag
import org.fourthline.cling.support.model.DIDLContent
import org.fourthline.cling.support.model.container.StorageFolder
import org.fourthline.cling.support.model.item.MusicTrack

/**
 * Created by jt on 1/13/18.
 */
class ClingBrowseImpl(private val service: Service<*, *>, private val controlPoint: ControlPoint) : ClingBrowse {

    override fun browse(containerId: String): Single<IndriDidl> {
        return Single.create { emitter ->
            val browseCallback = object : Browse(service, containerId, BrowseFlag.DIRECT_CHILDREN) {
                override fun updateStatus(ignored: Status?) {
                    //No-op.
                }

                override fun received(actionInvocation: ActionInvocation<out Service<*, *>>?, didl: DIDLContent?) {
                    emitter.onSuccess(didlToIndriDidl(containerId, didl!!))
                }

                override fun failure(invocation: ActionInvocation<out Service<*, *>>?, operation: UpnpResponse?, defaultMsg: String?) {
                    emitter.onError(RuntimeException("Browse failed; operation=$operation, defaultMsg=$defaultMsg"))
                }

            }

            val future = controlPoint.execute(browseCallback)

            emitter.setCancellable { future.cancel(false) }
        }
    }

    private fun didlToIndriDidl(containerId: String, didlContent: DIDLContent) : IndriDidl {
        val containers = didlContent.containers
                .filterIsInstance(StorageFolder::class.java)
                .map { IndriDidlContainer(ContainerId(containerId), ContainerId(it.id), it.title) }

        val items = didlContent.items
                .filterIsInstance(MusicTrack::class.java)
                .map { IndriDidlContent(ContainerId(containerId), ContentId(it.id), it.creator.orEmpty(), it.title, it.resources.first().duration, it.resources.first().value) }

        return IndriDidl(ContainerId(containerId), containers, items)
    }

}

package com.sibilantsolutions.indri.domain.usecase

import io.reactivex.Single
import org.fourthline.cling.model.action.ActionInvocation
import org.fourthline.cling.support.model.DIDLContent

/**
 * Created by jt on 1/13/18.
 */
interface ClingBrowse {

    fun browse(containerId: String) : Single<BrowseResult>

    data class BrowseResult(val actionInvocation: ActionInvocation<*>, val didl: DIDLContent)

}

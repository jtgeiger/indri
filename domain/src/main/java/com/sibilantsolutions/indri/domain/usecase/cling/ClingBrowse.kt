package com.sibilantsolutions.indri.domain.usecase.cling

import com.sibilantsolutions.indri.domain.model.IndriDidl
import io.reactivex.Single

/**
 * Created by jt on 1/13/18.
 */
interface ClingBrowse {

    fun browse(containerId: String) : Single<IndriDidl>

}

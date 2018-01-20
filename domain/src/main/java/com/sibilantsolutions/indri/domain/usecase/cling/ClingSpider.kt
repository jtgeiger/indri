package com.sibilantsolutions.indri.domain.usecase.cling

import com.sibilantsolutions.indri.domain.model.IndriDidl
import com.sibilantsolutions.indri.domain.model.IndriDidlContainer
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * Created by jt on 1/13/18.
 */
class ClingSpider(private val clingBrowse: ClingBrowse) {

    fun spider(containerId: String): Flowable<IndriDidl> {

        //Browse the root container.
        val rootResult: Single<IndriDidl> = clingBrowse.browse(containerId)

        val flowable: Flowable<IndriDidl> = rootResult.toFlowable()

        //Recurse on the root container to traverse the whole graph
        val allResults: Flowable<IndriDidl> =
                flowable.flatMap { recurse(it) }

        return allResults
    }

    private fun recurse(browseResult: IndriDidl) : Flowable<IndriDidl> {

        //Get all the containers from the DIDL; there may be zero or more.
        val didlContainers: Flowable<IndriDidlContainer> =
                Flowable.fromIterable(browseResult.containers)

        //For each storage folder, browse into each of its children.
        val children: Flowable<IndriDidl> =
                didlContainers
                        .flatMapSingle { clingBrowse.browse(it.localId.localId) }

        //Now recurse for each of the children.  If there were zero children, the recursion stops.
        val recurse: Flowable<IndriDidl> = children.flatMap { recurse(it) }

        //Finally concat the source result with the tree of results for its children.
        return Flowable.concat(Flowable.just(browseResult), recurse)
    }

}

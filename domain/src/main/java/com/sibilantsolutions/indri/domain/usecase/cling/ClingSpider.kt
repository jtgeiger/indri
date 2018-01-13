package com.sibilantsolutions.indri.domain.usecase.cling

import com.sibilantsolutions.indri.domain.usecase.cling.ClingBrowse.BrowseResult
import io.reactivex.Flowable
import io.reactivex.Single
import org.fourthline.cling.support.model.DIDLContent
import org.fourthline.cling.support.model.container.Container
import org.fourthline.cling.support.model.container.StorageFolder

/**
 * Created by jt on 1/13/18.
 */
class ClingSpider(private val clingBrowse: ClingBrowse) {

    fun spider(containerId: String): Flowable<DIDLContent> {

        //Browse the root container.
        val rootResult: Single<BrowseResult> = clingBrowse.browse(containerId)

        val flowable: Flowable<BrowseResult> = rootResult.toFlowable()

        //Recurse on the root container to traverse the whole graph
        val allResults: Flowable<BrowseResult> =
                flowable.flatMap { recurse(it) }

        //For convenience, return the DIDLs instead of the BrowseResult.
        return allResults.map { it.didl }
    }

    private fun recurse(browseResult: BrowseResult) : Flowable<BrowseResult> {

        //Get all the containers from the DIDL; there may be zero or more.
        val didlContainers: Flowable<Container> =
                Flowable.fromIterable(browseResult.didl.containers)

        //Get just the StorageFolder containers; there may be zero or more.
        val storageFolders: Flowable<StorageFolder> =
                didlContainers
                        .filter { StorageFolder.CLASS.equals(it) }
                        .cast(StorageFolder::class.java)

        //For each storage folder, browse into each of its children.
        val children: Flowable<BrowseResult> =
                storageFolders
                        .flatMapSingle { clingBrowse.browse(it.id) }

        //Now recurse for each of the children.  If there were zero children, the recursion stops.
        val recurse: Flowable<BrowseResult> = children.flatMap { recurse(it) }

        //Finally concat the source result with the tree of results for its children.
        return Flowable.concat(Flowable.just(browseResult), recurse)
    }

}

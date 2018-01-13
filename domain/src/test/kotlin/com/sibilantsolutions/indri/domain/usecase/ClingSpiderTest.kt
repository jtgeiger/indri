package com.sibilantsolutions.indri.domain.usecase

import com.sibilantsolutions.indri.domain.usecase.ClingBrowse.BrowseResult
import io.reactivex.Flowable
import io.reactivex.Single
import org.fourthline.cling.model.action.ActionInvocation
import org.fourthline.cling.model.meta.Action
import org.fourthline.cling.model.meta.Service
import org.fourthline.cling.support.model.DIDLContent
import org.fourthline.cling.support.model.container.StorageFolder
import org.fourthline.cling.support.model.item.MusicTrack
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito

/**
 * Created by jt on 1/13/18.
 */
class ClingSpiderTest {

    //Uppercase prefix indicates a folder.
    //lowercase prefix indicates a track.
    var map = mapOf<String, DIDLContent>(
            //"0" is the root, consisting of three folders and zero items.
            "0" to DIDLContent()
                    .addContainer(StorageFolder("0/A1_id", "0", "A1", null, null, null))
                    .addContainer(StorageFolder("0/A2_id", "0", "A2", null, null, null))
                    .addContainer(StorageFolder("0/A3_id", "0", "A3", null, null, null))
            ,
            //"0/A1_id" has one sub folder and three items.
            "0/A1_id" to DIDLContent()
                    .addContainer(StorageFolder("0/A1/B1_id", "0/A1_id", "A1:B1", null, null, null))
                    .addItem(MusicTrack("0/A1/a_id", "0/A1_id", "A1:a", null, null, null as String?, null))
                    .addItem(MusicTrack("0/A1/b_id", "0/A1_id", "A1:b", null, null, null as String?, null))
                    .addItem(MusicTrack("0/A1/c_id", "0/A1_id", "A1:c", null, null, null as String?, null))
            ,
            //"0/A1/B1_id" has zero sub folders and three items.  This is a leaf.
            "0/A1/B1_id" to DIDLContent()
                    .addItem(MusicTrack("0/A1/B1/a_id", "0/A1/B1_id", "A1:B1:a", null, null, null as String?, null))
                    .addItem(MusicTrack("0/A1/B1/b_id", "0/A1/B1_id", "A1:B1:b", null, null, null as String?, null))
                    .addItem(MusicTrack("0/A1/B1/c_id", "0/A1/B1_id", "A1:B1:c", null, null, null as String?, null))
            ,
            //"0/A2_id" has zero sub folders and three items.  This is a leaf.
            "0/A2_id" to DIDLContent()
                    .addItem(MusicTrack("0/A2/a_id", "0/A2_id", "A2:a", null, null, null as String?, null))
                    .addItem(MusicTrack("0/A2/b_id", "0/A2_id", "A2:b", null, null, null as String?, null))
                    .addItem(MusicTrack("0/A2/c_id", "0/A2_id", "A2:c", null, null, null as String?, null))
            ,
            //"0/A3_id" has one sub folders and three items.
            "0/A3_id" to DIDLContent()
                    .addContainer(StorageFolder("0/A3/B1_id", "0/A3_id", "A3:B1", null, null, null))
                    .addItem(MusicTrack("0/A3/a_id", "0/A3_id", "A3:a", null, null, null as String?, null))
                    .addItem(MusicTrack("0/A3/b_id", "0/A3_id", "A3:b", null, null, null as String?, null))
                    .addItem(MusicTrack("0/A3/c_id", "0/A3_id", "A3:c", null, null, null as String?, null))
            ,
            //"0/A3/B1_id" has one sub folders and zero items.
            "0/A3/B1_id" to DIDLContent()
                    .addContainer(StorageFolder("0/A3/B1/C1_id", "0/A3/B1_id", "A3:B1:C1", null, null, null))
            ,
            //"0/A3/B1/C1_id" has one zero folders and zero items.  This is an empty leaf.
            "0/A3/B1/C1_id" to DIDLContent()
    )

    @Test
    fun spider() {
        val clingBrowse = Mockito.mock(ClingBrowse::class.java)
        configClingBrowseMock(clingBrowse)

        val clingSpider = ClingSpider(clingBrowse)
        val result: Flowable<DIDLContent> = clingSpider.spider("0")

        val list: List<DIDLContent> = result.toList().blockingGet()

        //7 DIDLContent entries.
        assertEquals(list.toString(), 7, list.size)

        assertEquals(6, list.flatMap { it.containers }.size)

        val tracks = list.flatMap { it.items }

        assertEquals(12, tracks.size)


        //TODO: More tests.
    }

    private fun configClingBrowseMock(clingBrowse: ClingBrowse) {
        //When the ClingBrowse.browse(String) is called, look up the answer based on our map.
        Mockito.`when`(
                clingBrowse.browse(ArgumentMatchers.anyString()))
                .thenAnswer({ invocation ->
                    val arguments: Array<out Any> = invocation.arguments
                    val containerId = arguments[0] as String
                    lookup(containerId)
                })
    }

    private fun lookup(containerId: String) : Single<BrowseResult> {

        val didlContent = map[containerId]!!

        val browseResult = BrowseResult(ActionInvocation(Action<Service<*, *>>("fooAction", null)), didlContent)

        return Single.just(browseResult)
    }

}

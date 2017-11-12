package com.sibilantsolutions.indri.android

import io.reactivex.Observable
import org.fourthline.cling.support.model.DIDLContent
import org.fourthline.cling.support.model.container.StorageFolder
import org.fourthline.cling.support.model.item.MusicTrack
import java.io.Serializable

/**
 * Created by jt on 11/11/17.
 */

class SerializableDIDLContent(val containers: List<Container>, val items: List<Item>) : Serializable {

    abstract class Parent(val id: String, val parentId: String, val title: String) : Serializable

    class Container(id: String, parentId: String, title: String) : Parent(id, parentId, title)

    class Item(id: String, parentId: String, title: String, val creator: String, val resValue: String, val duration: String)
        : Parent(id, parentId, title)

    companion object {

        fun mapToSerializable(didl: DIDLContent): SerializableDIDLContent {
            val containers = Observable.fromIterable(didl.containers)
                    .filter { StorageFolder.CLASS.equals(it) }
                    .map { SerializableDIDLContent.Container(it.id, it.parentID, it.title) }
                    .toList()
                    .blockingGet()

            val itemTitles = Observable.fromIterable(didl.items)
                    .cast(MusicTrack::class.java)
                    .map { SerializableDIDLContent.Item(it.id, it.parentID, it.title, it.creator.orEmpty(),
                            it.resources.first().value, it.resources.first().duration) }
                    .toList()
                    .blockingGet()

            return SerializableDIDLContent(containers, itemTitles)
        }

    }
}
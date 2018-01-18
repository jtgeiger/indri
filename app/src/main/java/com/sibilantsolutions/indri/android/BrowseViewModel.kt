package com.sibilantsolutions.indri.android

/**
 * Created by jt on 1/17/18.
 */
class BrowseViewModel(val containers: List<Container>, val items: List<Item>, val serviceId: String) {

    abstract class AbstractBaseContent protected constructor(val id: String, val parentId: String, val title: String)

    class Container(id: String, parentId: String, title: String) : AbstractBaseContent(id, parentId, title)

    class Item(id: String, parentId: String, title: String, val creator: String, val resValue: String, val duration: String)
        : AbstractBaseContent(id, parentId, title)

}
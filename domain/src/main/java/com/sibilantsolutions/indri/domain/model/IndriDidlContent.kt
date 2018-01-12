package com.sibilantsolutions.indri.domain.model

import org.fourthline.cling.model.ServiceReference
import java.io.Serializable
import javax.inject.Singleton

/**
 * Class to model data that the UI may want to display and underlying data that would allow the
 * resource to be manipulated by UPNP.
 *
 * @property localId Identifier of this resource in the local repository; has nothing to do with
 * UPNP and is not useful outside this program.
 * @property performer String to display to the user represenging the performer (artist) of the
 * content.
 * @property title String to display to the user representing the title of the content.
 * @property didlResId The DIDL resource id representing the URL of the content.  This is given to the
 * renderer to tell it the content that it should fetch and play.
 */
data class IndriDidlContent(val localId: ContentId, val performer: String, val title: String, val didlResId: String)

/**
 * Class to model data that the UI may want to display and underlying data that would allow the
 * resource to be manipulated by UPNP.
 *
 * @property localId Identifier of this resource in the local repository; has nothing to do with
 * UPNP and is not useful outside this program.
 */
data class IndriDidlContainer(val localId: ContainerId, val title: String, val didlResId: String, val upnpService: ServiceReference)

/**
 * Type-safe wrapper of a numeric id representing playable content.  The wrapped id has nothing to
 * do with UPNP and is not useful outside this program.
 *
 * This is useful to pass across Android Activities via an Intent, where the target Activity can
 * retrieve the full data from a repository using this id.
 */
data class ContentId(val localId: Int) : Serializable

/**
 * Type-safe wrapper of a numeric id representing a container of content and other containers.  The
 * wrapped id has nothing to do with UPNP and is not useful outside this program.
 *
 * This is useful to pass across Android Activities via an Intent, where the target Activity can
 * retrieve the full data from a repository using this id.
 */
data class ContainerId(val localId: Int) : Serializable

@Singleton
class IndriDidlContentRepository() {

    var map = mutableMapOf<ContentId, IndriDidlContent>()

    fun get(contentId: ContentId) = map[contentId]

    fun put(contentId: ContentId, indriDidlContent: IndriDidlContent) {
        map[contentId] = indriDidlContent
    }
}

@Singleton
class IndriDidlContainerRepository() {

    var map = mutableMapOf<ContainerId, IndriDidlContainer>()

    fun get(containerId: ContainerId) = map[containerId]

    fun put(containerId: ContainerId, indriDidlContainer: IndriDidlContainer) {
        map[containerId] = indriDidlContainer
    }
}

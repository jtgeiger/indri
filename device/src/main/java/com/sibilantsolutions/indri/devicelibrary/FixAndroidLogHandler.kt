package com.sibilantsolutions.indri.devicelibrary

import org.fourthline.cling.android.FixedAndroidLogHandler

/**
 * Created by jt on 1/20/18.
 */
fun fixAndroidLogHandler() {
    // Fix the logging integration between java.util.logging and Android internal logging
    org.seamless.util.logging.LoggingUtil.resetRootHandler(
            FixedAndroidLogHandler()
    )
    // Now you can enable logging as needed for various categories of Cling:
    // Logger.getLogger("org.fourthline.cling").setLevel(Level.FINEST);

}

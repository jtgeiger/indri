package com.sibilantsolutions.indri.devicelibrary

import android.content.Context
import android.content.Intent
import org.fourthline.cling.android.AndroidUpnpServiceImpl

/**
 * Created by jt on 1/20/18.
 */
fun clingAndroidUpnpServiceIntent(context: Context): Intent {
    return Intent(context, AndroidUpnpServiceImpl::class.java)
}

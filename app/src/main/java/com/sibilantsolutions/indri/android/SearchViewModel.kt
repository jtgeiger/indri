package com.sibilantsolutions.indri.android

import android.view.View

/**
 * Created by jt on 1/16/18.
 */
class SearchViewModel(val entries: List<Entry>) {

    class Entry(val name: String, val clickListener: View.OnClickListener)
}

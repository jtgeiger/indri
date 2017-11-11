package com.sibilantsolutions.indri.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_browse.*
import org.fourthline.cling.support.model.DIDLContent

class BrowseActivity : AppCompatActivity() {

    companion object {

        private const val EXTRA_CONTAINERS = "EXTRA_CONTAINERS"
        private const val EXTRA_ITEMS = "EXTRA_ITEMS"

        fun newIntent(didl: DIDLContent, ctx: Context): Intent {
            val intent = Intent(ctx, BrowseActivity::class.java)

            //TODO: Create a SerializableDIDLContent class.  Or fetch from repository.

            val containerTitles = Observable.fromIterable(didl.containers).map { it.title }
                    .toList().map { ArrayList(it) }.blockingGet()
            intent.putStringArrayListExtra("${ctx.packageName}.$EXTRA_CONTAINERS", containerTitles)

            val itemTitles = Observable.fromIterable(didl.items).map { it.title }
                    .toList().map { ArrayList(it) }.blockingGet()
            intent.putStringArrayListExtra("${ctx.packageName}.$EXTRA_ITEMS", itemTitles)

            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browse)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val containerTitles = intent.getStringArrayListExtra("$packageName.$EXTRA_CONTAINERS")
        val itemTitles = intent.getStringArrayListExtra("$packageName.$EXTRA_ITEMS")

    }

}

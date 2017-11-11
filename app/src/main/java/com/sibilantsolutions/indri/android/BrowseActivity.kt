package com.sibilantsolutions.indri.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_browse.*
import kotlinx.android.synthetic.main.content_main.*
import org.fourthline.cling.android.AndroidUpnpServiceImpl
import org.fourthline.cling.support.model.DIDLContent
import org.fourthline.cling.support.model.container.StorageFolder

class BrowseActivity : AppCompatActivity() {

    private lateinit var browseContractPresenter: BrowseContract.Presenter

    companion object {

        private const val EXTRA_CONTAINERS = "EXTRA_CONTAINERS"
        private const val EXTRA_ITEMS = "EXTRA_ITEMS"

        fun newIntent(didl: DIDLContent, ctx: Context): Intent {
            val intent = Intent(ctx, BrowseActivity::class.java)

            //TODO: Create a SerializableDIDLContent class.  Or fetch from repository.

            val containerTitles = Observable.fromIterable(didl.containers).filter { StorageFolder.CLASS.equals(it) }.map { it.title }
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

        val browseContractView = fragment as BrowseActivityFragment
        browseContractPresenter = BrowsePresenter(browseContractView)
        browseContractView.presenter = browseContractPresenter as BrowsePresenter

        // This will start the UPnP service if it wasn't already started
        applicationContext.bindService(
                Intent(this, AndroidUpnpServiceImpl::class.java),
                browseContractPresenter.sc(),
                Context.BIND_AUTO_CREATE
        )

        val containerTitles = intent.getStringArrayListExtra("$packageName.$EXTRA_CONTAINERS")
        val itemTitles = intent.getStringArrayListExtra("$packageName.$EXTRA_ITEMS")

        browseContractPresenter.setContent(containerTitles, itemTitles)

    }

    override fun onDestroy() {
        super.onDestroy()
        // This will stop the UPnP service if nobody else is bound to it
        applicationContext.unbindService(browseContractPresenter.sc())
    }

}

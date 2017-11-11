package com.sibilantsolutions.indri.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.sibilantsolutions.indri.android.SerializableDIDLContent.Companion.mapToSerializable
import kotlinx.android.synthetic.main.activity_browse.*
import kotlinx.android.synthetic.main.content_main.*
import org.fourthline.cling.android.AndroidUpnpServiceImpl
import org.fourthline.cling.model.ServiceReference
import org.fourthline.cling.model.meta.Service
import org.fourthline.cling.support.model.DIDLContent

class BrowseActivity : AppCompatActivity() {

    private lateinit var browseContractPresenter: BrowseContract.Presenter

    companion object {

        private const val EXTRA_DIDL_CONTENT = "EXTRA_DIDL_CONTENT"
        private const val EXTRA_SERVICE_REFERENCE = "EXTRA_SERVICE_REFERENCE"

        fun newIntent(didl: DIDLContent, service: Service<*, *>, ctx: Context): Intent {
            val intent = Intent(ctx, BrowseActivity::class.java)

            val serializableDIDLContent = mapToSerializable(didl)

            intent.putExtra("${ctx.packageName}.$EXTRA_DIDL_CONTENT", serializableDIDLContent)

            intent.putExtra("${ctx.packageName}.$EXTRA_SERVICE_REFERENCE", service.reference.toString())

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

        val serializableDIDLContent = intent.getSerializableExtra("${packageName}.$EXTRA_DIDL_CONTENT")
                as SerializableDIDLContent
        val serviceReference = ServiceReference(intent.getStringExtra("${packageName}.$EXTRA_SERVICE_REFERENCE"))

        browseContractPresenter.setContent(serializableDIDLContent, serviceReference)

    }

    override fun onDestroy() {
        super.onDestroy()
        // This will stop the UPnP service if nobody else is bound to it
        applicationContext.unbindService(browseContractPresenter.sc())
    }

}

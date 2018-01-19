package com.sibilantsolutions.indri.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_browse.*
import kotlinx.android.synthetic.main.content_main.*
import org.fourthline.cling.android.AndroidUpnpServiceImpl

class BrowseActivity : AppCompatActivity() {

    private lateinit var browseContractPresenter: BrowseContract.Presenter

    companion object {

        private const val EXTRA_CONTAINER_ID = "EXTRA_CONTAINER_ID"
        private const val EXTRA_SERVICE_ID = "EXTRA_SERVICE_ID"

        fun newIntent(containerId: String, serviceId: String, ctx: Context): Intent {
            val intent = Intent(ctx, BrowseActivity::class.java)

            intent.putExtra("${ctx.packageName}.$EXTRA_CONTAINER_ID", containerId)

            intent.putExtra("${ctx.packageName}.$EXTRA_SERVICE_ID", serviceId)

            return intent
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browse)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { browseContractPresenter.spider() }

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

        val containerId = intent.getStringExtra("$packageName.$EXTRA_CONTAINER_ID")
        val serviceId = intent.getStringExtra("$packageName.$EXTRA_SERVICE_ID")

        class MyPair(val containerId: String, val serviceId: String)

        //HACK: The service isn't connected yet, i.e. the ServiceConnection hasn't been fired yet.
        // This seems to happen on the UI thread after this point.  So, get off the main thread to
        // let that finish.  Then get back on it, presumably after that's done.
        Single.just(MyPair(containerId, serviceId))
                //Get off the main thread to let it finish connecting the service.
                .subscribeOn(Schedulers.computation())
                //Get right back on so our task will get queued on main's looper.
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { it -> browseContractPresenter.browse(it.containerId, it.serviceId) }
    }

    override fun onDestroy() {
        super.onDestroy()
        // This will stop the UPnP service if nobody else is bound to it
        applicationContext.unbindService(browseContractPresenter.sc())
    }

}

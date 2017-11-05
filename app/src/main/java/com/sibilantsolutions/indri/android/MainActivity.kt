package com.sibilantsolutions.indri.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.fourthline.cling.android.AndroidUpnpServiceImpl
import org.fourthline.cling.android.FixedAndroidLogHandler

class MainActivity : AppCompatActivity() {

    private lateinit var searchContractPresenter: SearchContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val searchContractView = fragment as SearchContract.View
        searchContractPresenter = SearchPresenter(searchContractView)

        fab.setOnClickListener { view ->
            searchContractPresenter.search()
        }

        // Fix the logging integration between java.util.logging and Android internal logging
        org.seamless.util.logging.LoggingUtil.resetRootHandler(
                FixedAndroidLogHandler()
        )
        // Now you can enable logging as needed for various categories of Cling:
        // Logger.getLogger("org.fourthline.cling").setLevel(Level.FINEST);

        // This will start the UPnP service if it wasn't already started
        applicationContext.bindService(
                Intent(this, AndroidUpnpServiceImpl::class.java),
                searchContractPresenter.sc(),
                Context.BIND_AUTO_CREATE
        )

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        searchContractPresenter.onDestroy()
        // This will stop the UPnP service if nobody else is bound to it
        applicationContext.unbindService(searchContractPresenter.sc())
    }

}

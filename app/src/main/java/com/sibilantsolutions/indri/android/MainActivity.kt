package com.sibilantsolutions.indri.android

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.sibilantsolutions.indri.devicelibrary.ObservableServiceBinder
import com.sibilantsolutions.indri.devicelibrary.fixAndroidLogHandler
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var searchContractPresenter: SearchContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // Fix the logging integration between java.util.logging and Android internal logging
        fixAndroidLogHandler()

        val searchContractView = fragment as MainActivityFragment

        searchContractPresenter = SearchPresenter(searchContractView,
                ObservableServiceBinder().bindService(this))

        searchContractView.presenter = searchContractPresenter

        fab.setOnClickListener { searchContractPresenter.search() }
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
    }

}

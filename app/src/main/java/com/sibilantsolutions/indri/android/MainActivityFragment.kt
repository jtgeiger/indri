package com.sibilantsolutions.indri.android

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.recycler_view_row.view.*
import org.fourthline.cling.model.meta.Device
import org.fourthline.cling.model.meta.Service
import org.fourthline.cling.model.types.UDAServiceType
import org.fourthline.cling.support.model.DIDLContent


class MainActivityFragment : Fragment(), SearchContract.View {

    lateinit var presenter: SearchContract.Presenter

    private val myAdapter = MyAdapter(arrayListOf())

    override fun addDevice(device: Device<*, *, *>) {
        myAdapter.devices.add(device)
//        val lastIndex = myAdapter.devices.lastIndex
//        myAdapter.notifyItemInserted(lastIndex)
//        myAdapter.notifyItemChanged(lastIndex)
        myAdapter.notifyDataSetChanged()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler_view.layoutManager = LinearLayoutManager(context)

        recycler_view.setHasFixedSize(true)

        recycler_view.adapter = myAdapter
    }

    override fun snackbar(msg: String) {
        Snackbar.make(view!!, msg, Snackbar.LENGTH_LONG).setAction("Action", null).show()
    }

    override fun show(didl: DIDLContent, service: Service<*, *>) {
        startActivity(BrowseActivity.newIntent(didl, service, context))
    }

    private inner class MyAdapter(val devices: MutableList<Device<*, *, *>>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

        init {
            setHasStableIds(true)
        }

        override fun getItemId(position: Int): Long {
            return devices[position].identity.udn.hashCode().toLong()
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val device = devices[position]
            holder.bind(device)
            if (device.findService(UDAServiceType("ContentDirectory")) != null) {
                holder.itemView.setOnClickListener { presenter.browse(device) }
            }
        }

        override fun getItemCount(): Int {
            return devices.size
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyViewHolder {
            val myRow = LayoutInflater.from(parent!!.context).inflate(R.layout.recycler_view_row, parent, false)

            return MyViewHolder(myRow)
        }

        private inner class MyViewHolder(myRow: View) : RecyclerView.ViewHolder(myRow) {
            //Cache the child view(s) because the Kotlin Android Extension won't currently cache custom views and
            // always use findViewById under the hood.  Could instead implement LayoutContainer but that requires
            // enabling experimental features and I don't currently want to do that.
            private val textView: TextView = myRow.textView
            private val textView2: TextView = myRow.textView2
            private val textView3: TextView = myRow.textView3
            private val textView4: TextView = myRow.textView4

            fun bind(device: Device<*, *, *>) {
                textView.text = device.details.friendlyName
                textView2.text = device.embeddedDevices.size.toString()
                textView3.text = device.services.size.toString()
                textView4.text = (device.findService(UDAServiceType("ContentDirectory")) != null).toString()
            }
        }

    }

}

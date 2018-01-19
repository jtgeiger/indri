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


class MainActivityFragment : Fragment(), SearchContract.View {

    lateinit var presenter: SearchContract.Presenter

    private val myAdapter = MyAdapter(arrayListOf())

    override fun render(searchViewModel: SearchViewModel) {
        myAdapter.entries.clear()
        myAdapter.entries.addAll(searchViewModel.entries)
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

    override fun show(containerId: String, serviceId: String) {
        startActivity(BrowseActivity.newIntent(containerId, serviceId, context))
    }

    private inner class MyAdapter(val entries: MutableList<SearchViewModel.Entry>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

        init {
            setHasStableIds(true)
        }

        override fun getItemId(position: Int): Long {
            return entries[position].hashCode().toLong()
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val entry = entries[position]
            holder.bind(entry)
        }

        override fun getItemCount(): Int {
            return entries.size
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
//            private val textView2: TextView = myRow.textView2
//            private val textView3: TextView = myRow.textView3
//            private val textView4: TextView = myRow.textView4

            fun bind(entry: SearchViewModel.Entry) {
                textView.text = entry.name
//                textView2.text = device.embeddedDevices.size.toString()
//                textView3.text = device.services.size.toString()
//                textView4.text = (device.findService(UDAServiceType("ContentDirectory")) != null).toString()

                itemView.setOnClickListener(entry.clickListener)
            }
        }

    }

}

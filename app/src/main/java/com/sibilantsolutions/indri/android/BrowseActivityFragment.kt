package com.sibilantsolutions.indri.android

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.recycler_view_row.view.*
import org.fourthline.cling.model.ServiceReference

/**
 * A placeholder fragment containing a simple view.
 */
class BrowseActivityFragment : Fragment(), BrowseContract.View {

    lateinit var presenter: BrowseContract.Presenter

    companion object Consts {
        private const val CONTAINER_TYPE = 11
        private const val ITEM_TYPE = 21
    }

    private val myAdapter = MyAdapter(arrayListOf())

    inner class MyAdapter(val list: MutableList<SerializableDIDLContent.Parent>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        lateinit var serviceReference: ServiceReference

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

            return when (viewType) {
                CONTAINER_TYPE -> MyContainerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_row, parent, false))
                ITEM_TYPE -> MyItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_row, parent, false))
                else -> throw RuntimeException("Unexpected viewtype=${viewType}")
            }
        }

        override fun getItemViewType(position: Int): Int {
            return when (list[position]) {
                is SerializableDIDLContent.Container -> CONTAINER_TYPE
                is SerializableDIDLContent.Item -> ITEM_TYPE
                else -> throw RuntimeException("Unexpected type")
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val item = list[position]
            when (item) {
                is SerializableDIDLContent.Container -> {
                    (holder as MyContainerViewHolder).bind(item)
                    holder.itemView.setOnClickListener { presenter.browse(item.id, serviceReference) } }
                is SerializableDIDLContent.Item -> (holder as MyItemViewHolder).bind(item)
                else -> throw RuntimeException("Unexpected item type")
            }
        }

        override fun getItemCount() = list.size

    }

    class MyContainerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //Cache the child view(s) because the Kotlin Android Extension won't currently cache custom views and
        // always use findViewById under the hood.  Could instead implement LayoutContainer but that requires
        // enabling experimental features and I don't currently want to do that.
        private val textView: TextView = itemView.textView
//        private val textView2: TextView = myRow.textView2
//        private val textView3: TextView = myRow.textView3
//        private val textView4: TextView = myRow.textView4

        fun bind(item: SerializableDIDLContent.Container) {
            textView.text = item.title
//            textView2.text = device.embeddedDevices.size.toString()
//            textView3.text = device.services.size.toString()
//            textView4.text = (device.findService(UDAServiceType("ContentDirectory")) != null).toString()
        }
    }

    class MyItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //Cache the child view(s) because the Kotlin Android Extension won't currently cache custom views and
        // always use findViewById under the hood.  Could instead implement LayoutContainer but that requires
        // enabling experimental features and I don't currently want to do that.
        private val textView: TextView = itemView.textView
//        private val textView2: TextView = myRow.textView2
//        private val textView3: TextView = myRow.textView3
//        private val textView4: TextView = myRow.textView4

        fun bind(item: SerializableDIDLContent.Item) {
            textView.text = item.title
//            textView2.text = device.embeddedDevices.size.toString()
//            textView3.text = device.services.size.toString()
//            textView4.text = (device.findService(UDAServiceType("ContentDirectory")) != null).toString()
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_browse, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler_view.layoutManager = LinearLayoutManager(context)

//        recycler_view.setHasFixedSize(true)

        recycler_view.adapter = myAdapter
    }

    override fun setContent(serializableDIDLContent: SerializableDIDLContent, serviceReference: ServiceReference) {

        myAdapter.serviceReference = serviceReference
        myAdapter.list.clear()
        myAdapter.list.addAll(serializableDIDLContent.containers)
        myAdapter.list.addAll(serializableDIDLContent.items)
        myAdapter.notifyDataSetChanged()
    }

}

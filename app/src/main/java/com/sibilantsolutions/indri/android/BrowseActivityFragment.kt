package com.sibilantsolutions.indri.android

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.recycler_view_row.view.*

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

    inner class MyAdapter(val list: MutableList<MyBaseContent>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

            return when (viewType) {
                CONTAINER_TYPE -> MyContainerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_row, parent, false))
                ITEM_TYPE -> MyItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_row, parent, false))
                else -> throw RuntimeException("Unexpected viewtype=${viewType}")
            }
        }

        override fun getItemViewType(position: Int): Int {
            return when (list[position]) {
                is MyContainer -> CONTAINER_TYPE
                is MyItem -> ITEM_TYPE
                else -> throw RuntimeException("Unexpected type")
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val item = list[position]
            when (item) {
                is MyContainer -> { (holder as MyContainerViewHolder).bind(item); holder.itemView.setOnClickListener { presenter.browse(item.id) } }
                is MyItem -> (holder as MyItemViewHolder).bind(item)
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

        fun bind(item: MyContainer) {
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

        fun bind(item: MyItem) {
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

    override fun setContent(containerTitles: List<String>, itemTitles: List<String>) {
        myAdapter.list.clear()
        //TODO: Fix how these are being created to use the correct values.
        Observable.fromIterable(containerTitles).map { MyContainer(it, "fake1", "fake2") }
                .forEach { myAdapter.list.add(it) }
        Observable.fromIterable(itemTitles).map { MyItem(it, "fake1", "fake2") }
                .forEach { myAdapter.list.add(it) }
        myAdapter.notifyDataSetChanged()
    }

    abstract class MyBaseContent(val title: String, val id: String, val parentId: String)

    class MyContainer(title: String, id: String, parentId: String) : MyBaseContent(title, id, parentId)
    class MyItem(title: String, id: String, parentId: String) : MyBaseContent(title, id, parentId)

}

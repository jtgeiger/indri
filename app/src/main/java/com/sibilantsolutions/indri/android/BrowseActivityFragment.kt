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
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.recycler_view_row.view.*

/**
 * A placeholder fragment containing a simple view.
 */
class BrowseActivityFragment : Fragment(), BrowseContract.View {

    lateinit var presenter: BrowseContract.Presenter

    //TODO: How to clean this up for lifecycle changes?
    private val browseObservable: Observable<String>
    private val browseObserver: Observer<String>

    init {
        val subject = PublishSubject.create<String>()
        browseObservable = subject
        browseObserver = subject
    }

    companion object Consts {
        private const val CONTAINER_TYPE = 11
        private const val ITEM_TYPE = 21
    }

    private val myAdapter = MyAdapter(arrayListOf())

    inner class MyAdapter(val list: MutableList<BrowseViewModel.AbstractBaseContent>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

            return when (viewType) {
                CONTAINER_TYPE -> MyContainerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_row, parent, false))
                ITEM_TYPE -> MyItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_row, parent, false))
                else -> throw RuntimeException("Unexpected viewtype=${viewType}")
            }
        }

        override fun getItemViewType(position: Int): Int {
            return when (list[position]) {
                is BrowseViewModel.Container -> CONTAINER_TYPE
                is BrowseViewModel.Item -> ITEM_TYPE
                else -> throw RuntimeException("Unexpected type")
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val item = list[position]
            when (item) {
                is BrowseViewModel.Container -> {
                    (holder as MyContainerViewHolder).bind(item)
                    holder.itemView.setOnClickListener { browseObserver.onNext(item.id) } }
                is BrowseViewModel.Item -> {
                    (holder as MyItemViewHolder).bind(item)
                    holder.itemView.setOnClickListener { presenter.play(item.resValue) }
                }
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

        fun bind(item: BrowseViewModel.Container) {
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
        private val textView2: TextView = itemView.textView2
        private val textView3: TextView = itemView.textView3
//        private val textView4: TextView = itemView.textView4

        fun bind(item: BrowseViewModel.Item) {
            textView.text = item.creator
            textView2.text = item.title
            textView3.text = "(${item.duration})"
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

    override fun render(browseViewModel: BrowseViewModel) {
        myAdapter.list.clear()
        myAdapter.list.addAll(browseViewModel.containers)
        myAdapter.list.addAll(browseViewModel.items)
        myAdapter.notifyDataSetChanged()
    }

    override fun browseObservable(): Observable<String> =
            browseObservable.observeOn(Schedulers.io())

    override fun snackbar(msg: String) {
        Snackbar.make(view!!, msg, Snackbar.LENGTH_LONG).setAction("Action", null).show()
    }

}

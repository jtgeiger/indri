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


class MainActivityFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler_view.layoutManager = LinearLayoutManager(context)

        recycler_view.setHasFixedSize(true)

        val myAdapter = MyAdapter(arrayListOf("Foo", "Bar"))
        recycler_view.adapter = myAdapter
    }

    private class MyAdapter(val strs: MutableList<String>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.bind(strs[position])
        }

        override fun getItemCount(): Int {
            return strs.size
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyViewHolder {
            val myRow = LayoutInflater.from(parent!!.context).inflate(R.layout.recycler_view_row, parent, false)

            return MyViewHolder(myRow)
        }

        private class MyViewHolder(myRow: View) : RecyclerView.ViewHolder(myRow) {
            //Cache the child view(s) because the Kotlin Android Extension won't currently cache custom views and
            // always use findViewById under the hood.  Could instead implement LayoutContainer but that requires
            // enabling experimental features and I don't currently want to do that.
            private val textView: TextView = myRow.textView

            fun bind(s: String) {
                textView.text = s
            }
        }

    }

}

package com.example.apolusov.kotlintest

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_new.view.*

class SimpleAdapter : RecyclerView.Adapter<SimpleAdapter.SimpleViewHolder>() {

    private val EMPTY_VIEW = 0
    private val CONTENT_VIEW = 1

    private var data = (0..9).map { "item here is some space number = $it" }

    override fun getItemViewType(position: Int): Int {
        when (position) {
//            0 -> return EMPTY_VIEW
//            9 -> return EMPTY_VIEW
            else -> return CONTENT_VIEW
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
        val layoutRes = when (viewType) {
            EMPTY_VIEW -> R.layout.item_empty
            else ->  R.layout.item_new
        }

        val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
        return SimpleViewHolder(view)
    }

    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
        holder.bind(data.get(position))
    }

    class SimpleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: String) {
            itemView.someText?.text = item
        }
    }

    override fun getItemCount() = data.size
}
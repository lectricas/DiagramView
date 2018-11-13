package com.example.apolusov.kotlintest.somenew

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.apolusov.kotlintest.R
import com.example.apolusov.kotlintest.old.PointD
import com.example.apolusov.kotlintest.withadapter.CustomRecyclerView
import com.example.apolusov.kotlintest.withadapter.DayData
import com.example.apolusov.kotlintest.withadapter.GraphicView
import kotlinx.android.synthetic.main.item_new.view.*
import timber.log.Timber

class NewAdapter : RecyclerView.Adapter<NewAdapter.NewViewHolder>() {

    var data = (0..10).map { dayCount ->
        "$dayCount + someString"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewViewHolder {
        Timber.d("adapter onCreateViewHolder")
        return NewAdapter.NewViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_new,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: NewViewHolder, position: Int) {
        Timber.d("adapter onBindViewholder")
        holder.bindItems(data[position])
    }

    class NewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(day: String) {
            itemView.someText.text = day
        }
    }

    override fun getItemCount() = data.size
}
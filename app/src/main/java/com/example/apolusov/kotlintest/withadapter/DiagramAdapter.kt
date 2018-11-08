package com.example.apolusov.kotlintest.withadapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.apolusov.kotlintest.PointD
import com.example.apolusov.kotlintest.R


class DiagramAdapter : RecyclerView.Adapter<DiagramAdapter.ViewHolder>() {

//    var data = (0..9).map { dayCount ->
//        DayData((0..20).map { PointD(it.t, 5, it) })
//    }

    var data = mutableListOf<DayData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        DiagramAdapter.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_day, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(data[position])
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(day: DayData) {

        }
    }

    override fun getItemCount() = data.size
}
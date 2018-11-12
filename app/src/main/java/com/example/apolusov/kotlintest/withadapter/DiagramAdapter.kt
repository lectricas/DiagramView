package com.example.apolusov.kotlintest.withadapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.apolusov.kotlintest.PointD
import com.example.apolusov.kotlintest.R
import timber.log.Timber


class DiagramAdapter : RecyclerView.Adapter<DiagramAdapter.DiagramViewHolder>() {

    lateinit var recyclerView: CustomRecyclerView

    var data = (0..10).map { dayCount ->
        DayData((0..20).map { PointD(it.toFloat(), 5f, it) })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiagramViewHolder {
        Timber.d("createViewHolder $viewType")
        return DiagramAdapter.DiagramViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_day,
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: DiagramViewHolder, position: Int) {
        holder.bindItems(data[position], recyclerView.viewWidth, recyclerView.customDigit, position)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView as CustomRecyclerView
    }

    class DiagramViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(day: DayData, viewWidth: Int, customDigit: Int, position: Int) {
            Timber.d("$position bind")
            (itemView as GraphicView).position = position
            itemView.digit = customDigit
            itemView.requestLayout()
        }
    }

    override fun getItemCount() = data.size
}
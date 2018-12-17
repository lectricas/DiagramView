package com.example.apolusov.kotlintest

import android.content.Context
import android.view.View
import com.example.apolusov.kotlintest.diagram.DataPoint

class CustomViewTest: View {

    val pointsWidth = 10
    val pointsHeight = 5

    var dataPoints = listOf<DataPoint>()

    constructor(context: Context) : super(context) {

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(width, height)

    }

    fun setData(data: List<DataPoint>) {
        this.dataPoints = data
        requestLayout()
    }
}
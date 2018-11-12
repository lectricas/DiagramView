package com.example.apolusov.kotlintest.linear

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View

class InnerView : View {

    val paint = Paint().apply {
        color = Color.BLACK
        textSize = 100f
    }

    var number = 0

    constructor(context: Context) : super(context, null, 0) {
//        scaleDetector = ScaleGestureDetector(context, scaleListener)
    }


    override fun onDraw(canvas: Canvas) {
        canvas.drawText("$number", 500f, 500f, paint)
    }
}


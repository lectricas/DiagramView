package com.example.apolusov.kotlintest

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.widget.TextView

class EmptyCustomView : TextView {

    val path = Path()
    val paint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 3f
        textSize = 50f
    }

    var scaleFactor = 1f

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, attributeSetId: Int) : super(context, attrs, attributeSetId)

    override fun onDraw(canvas: Canvas) {
        canvas.scale( 0.5f, 1f, width/ 2f, height/2f)
        canvas.drawText("SOMETEXT HERE ${App.scaleFactor}", width/2f, height/2f, paint)

    }
}
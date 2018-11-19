package com.example.apolusov.kotlintest

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.*


class CustomView : View {

    val path = Path()
    val paint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 3f
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, attributeSetId: Int) : super(context, attrs, attributeSetId)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        path.moveTo(0f, height.toFloat());
        path.cubicTo(
            width.toFloat(),
            height.toFloat(),
            0f,
            0f,
            width.toFloat(),
            0f);
        canvas.drawPath(path, paint);
    }
}
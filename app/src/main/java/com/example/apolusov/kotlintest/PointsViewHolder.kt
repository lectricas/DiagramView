package com.example.apolusov.kotlintest

import android.graphics.RectF
import java.util.*

class PointsViewHolder(
    var left: Float,
    var top: Float,
    var right: Float,
    var bottom: Float,
    val calendar: Calendar
) {

    fun middleX() = (left + right) * 0.5f
    fun middleY() = (top + bottom) * 0.5f

    fun offsetX(scrollBy: Float) {
        left += scrollBy
        right += scrollBy
        val rectF = RectF()
        rectF.centerX()
    }
}
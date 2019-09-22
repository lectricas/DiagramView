package com.polusov.infinitediagram.diagram

import android.graphics.RectF

data class RectView(
    val rectF: RectF,
    val data: Int,
    val color: Int,
    val points: List<RectPoint> = listOf()
) {
    data class RectPoint(
        val x: Float,
        val y: Float
    )
}
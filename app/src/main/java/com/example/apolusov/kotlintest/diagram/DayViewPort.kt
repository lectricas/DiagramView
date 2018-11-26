package com.example.apolusov.kotlintest.diagram

import android.graphics.Path
import android.graphics.RectF
import com.firstlinesoftware.diabetus.diagram.DayItem
import com.firstlinesoftware.diabetus.diagram.DiagramPoint

class DayViewPort(
    val rectF: RectF,
    val points: List<DiagramPoint>,
    val text: String
) {
    companion object {

        private const val maxViewWidthPoints = 24f
        private const val maxViewHeightPoints = 5f

        fun construct(
            rectF: RectF,
            dayItem: DayItem
        ): DayViewPort {


            return DayViewPort(
                rectF,
                dayItem.points.map {
                    it.convert(
                        maxViewWidthPoints,
                        rectF.width(),
                        maxViewHeightPoints,
                        rectF.height(),
                        rectF.left
                    )
                },
                dayItem.dayNumber.toString()
            )
        }
    }

    fun scaleHorizontally(factor: Float, viewWidthInPixels: Float) {
        val sLeft = (rectF.left - viewWidthInPixels / 2) * factor
        val sRight = (rectF.right - viewWidthInPixels / 2) * factor
        rectF.left = sLeft + viewWidthInPixels / 2
        rectF.right = sRight + viewWidthInPixels / 2
        points.forEach { it.scale(factor, viewWidthInPixels) }
    }

    fun offsetChildrenHorizontal(scrollBy: Float) {
        rectF.offset(scrollBy, 0f)
        points.forEach { it.offset(scrollBy) }
    }

//    fun scaleVertically(factor: Float) {
//        val sTop = (rectF.top - viewHeightInPixels / 2) * factor
//        val sBottom = (rectF.bottom - viewHeightInPixels / 2) * factor
//        rectF.top = sTop + viewHeightInPixels /2
//        rectF.bottom = sBottom + viewHeightInPixels /2
//    }
}
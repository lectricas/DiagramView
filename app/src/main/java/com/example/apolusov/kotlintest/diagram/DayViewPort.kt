package com.example.apolusov.kotlintest.diagram

import android.graphics.RectF
import com.firstlinesoftware.diabetus.diagram.DayItem
import com.firstlinesoftware.diabetus.diagram.DiagramPoint

class DayViewPort(
    val rectF: RectF,
    val before: List<DiagramPoint>,
    val now: List<DiagramPoint>,
    val after: List<DiagramPoint>,
    val text: String
) {
    companion object {

        private const val maxViewWidthPoints = 24f
        private const val maxViewHeightPoints = 5f

        fun construct(rectF: RectF, dayItem: DayItem): DayViewPort {
            return DayViewPort(
                rectF,
                dayItem.before.map { it.convert(maxViewWidthPoints, rectF.width(), maxViewHeightPoints, rectF.height()) },
                dayItem.now.map { it.convert(maxViewWidthPoints, rectF.width(), maxViewHeightPoints, rectF.height()) },
                dayItem.after.map { it.convert(maxViewWidthPoints, rectF.width(), maxViewHeightPoints, rectF.height()) },
                dayItem.dayNumber.toString()
            )
        }
    }
}
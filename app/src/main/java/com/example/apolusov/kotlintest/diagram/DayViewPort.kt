package com.example.apolusov.kotlintest.diagram

import android.graphics.RectF
import com.firstlinesoftware.diabetus.diagram.DayItem
import com.firstlinesoftware.diabetus.diagram.DiagramPoint

class DayViewPort(
    val rectF: RectF,
    val before: List<DiagramPoint>,
    val now: List<DiagramPoint>,
    val after: List<DiagramPoint>
) {
    companion object {
        fun construct(rectF: RectF, dayItem: DayItem): DayViewPort {
            return DayViewPort(
                rectF,
                dayItem.before,
                dayItem.now,
                dayItem.after
            )
        }
    }
}
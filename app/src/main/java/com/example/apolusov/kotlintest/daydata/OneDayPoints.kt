package com.example.apolusov.kotlintest.daydata

import java.util.*

class OneDayPoints(
//    val currentDay: List<DiabetPoint>,
//    val dayBefore: List<DiabetPoint>?,
//    val dayAfter: List<DiabetPoint>?
    val dayBefore: Calendar?,
    val currentDay: Calendar,
    val dayAfter: Calendar?,
    var leftBorderX: Float = 0f,
    var rightBorderX: Float = 0f,
    var height: Float = 0f
)
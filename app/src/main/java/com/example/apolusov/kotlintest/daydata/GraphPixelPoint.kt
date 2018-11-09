package com.example.apolusov.kotlintest.daydata

import java.util.*

class GraphPixelPoint(
    val x: Float,
    val y: Float,
    val substanceAmount: Int,
    val type: DotColor,
    val calendar: Calendar
) {
    override fun toString(): String {
        return "[$x, $y, $type, ${calendar.timeInMillis}, ${calendar.get(Calendar.HOUR_OF_DAY)}]"
    }
}

//1541364159939
//1541364159939
//1541364159939
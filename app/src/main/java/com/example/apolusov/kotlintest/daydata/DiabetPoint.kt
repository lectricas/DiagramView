package com.example.apolusov.kotlintest.daydata

import java.util.*

class DiabetPoint(
    val time: Long,
    val substanceAmount: Int,
    val type: DotColor,
    val calendar: Calendar
) {
    override fun toString(): String {
        return "[$time, $substanceAmount, $type, ${calendar.timeInMillis}, ${calendar.get(Calendar.HOUR_OF_DAY)}]"
    }
}
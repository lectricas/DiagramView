package com.example.apolusov.kotlintest.diagram

import java.util.*

data class DataPoint(
    val valueX: Int,
    val valueY: Int
) {

    override fun toString(): String {
        return "[$valueX, $valueY]"
    }
}
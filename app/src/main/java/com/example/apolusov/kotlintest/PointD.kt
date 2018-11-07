package com.example.apolusov.kotlintest

data class PointD(
    val x: Float,
    val y: Float,
    val text: Float
) {
    override fun toString() = "[$x, $y, $text]"
}
package com.example.apolusov.kotlintest

data class PointD(
    val x: Float,
    val y: Float
) {
    override fun toString() = "[$x, $y]"
}
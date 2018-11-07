package com.example.apolusov.kotlintest

data class PointM(
    val x: Float,
    val y: Float
) {
    override fun toString() = "[$x, $y]"
}
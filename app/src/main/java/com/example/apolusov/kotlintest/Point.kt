package com.example.apolusov.kotlintest

data class Point(
    val x: Float,
    val y: Float,
    val pixelX: Float,
    val pixelY: Float
) {
    override fun toString() = "[$x, $y, $pixelX, $pixelY]"
}
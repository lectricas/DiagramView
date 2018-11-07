package com.example.apolusov.kotlintest

data class PointM(
    val x: Int,
    val y: Int
) {
    override fun toString() = "[$x, $y]"
}
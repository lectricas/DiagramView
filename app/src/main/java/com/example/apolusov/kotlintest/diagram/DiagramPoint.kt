package com.firstlinesoftware.diabetus.diagram

class DiagramPoint(
    val x: Float,
    val y: Float,
    val type: Int,
    val text: String
) {

    fun convert(
        oldWidth: Float,
        newWidth: Float,
        oldHight: Float,
        newHight: Float
    ): DiagramPoint {
        return DiagramPoint(
            getCalculatedX(this.x, oldWidth, newWidth),
            getCalculatedY(this.y, oldHight, newHight),
            this.type,
            this.text
        )
    }

    //move from real world coordinates to the viewport and vice versa
    fun getCalculatedX(oldX: Float, oldWidth: Float, newWidth: Float): Float {
        return oldX / oldWidth * newWidth
    }

    //move from real world coordinates to the viewport and vice versa
    fun getCalculatedY(oldY: Float, oldHight: Float, newHight: Float): Float {
        return newHight - oldY / oldHight * newHight
    }
}
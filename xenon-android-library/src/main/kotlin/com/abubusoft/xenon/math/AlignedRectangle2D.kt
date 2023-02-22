package com.abubusoft.xenon.math

/**
 * Rappresenta un rettangolo allineato con gli assi.
 *
 * @author Francesco Benincasa
 */
class AlignedRectangle2D(centerX: Float, centerY: Float, widthValue: Float, heightValue: Float) {
    /**
     * centro del rettangolo
     */
    val center: Point2

    /**
     * altezza
     */
    var height: Float

    /**
     * larghezza
     */
    var width: Float

    init {
        center = Point2(centerX, centerY)
        width = widthValue
        height = heightValue
    }

    /**
     * Verifica se un cerchio interseca con il rettangolo
     *
     * @param circle
     * @return
     */
    fun intersect(circle: Circle): Boolean {
        return if ((Math.abs(circle.center.x - center.x) <= width / 2 + circle.radius && Math.abs(circle.center.y - center.y) <= height / 2) + circle.radius) true else false
    }

    /**
     * @return
     */
    fun copy(): AlignedRectangle2D {
        return AlignedRectangle2D(center.x, center.y, width, height)
    }

    /**
     * @param destination
     */
    fun copyInto(destination: AlignedRectangle2D) {
        center.copyInto(destination.center)
        destination.width = width
        destination.height = height
    }
}
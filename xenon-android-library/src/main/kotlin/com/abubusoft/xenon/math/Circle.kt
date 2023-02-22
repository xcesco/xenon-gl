/**
 *
 */
package com.abubusoft.xenon.math

/**
 * @author Francesco Benincasa
 */
class Circle {
    constructor() {
        center = Point2()
    }

    constructor(x: Float, y: Float, radius: Float) {
        center = Point2()
        center.x = x
        center.y = y
        this.radius = radius
    }

    val center: Point2

    /**
     * raggio del cerchio
     */
    var radius = 0f

    /**
     * Indica se i due cerchi si intersecano.
     *
     * Se la distanza tra i due centri è minore o uguale alla somma dei due raggi, allora si
     * intersecano necessariamente.
     *
     * @param circle2
     * @return
     * true se collidono, false altrimenti
     */
    fun intersect(circle2: Circle): Boolean {
        val distance = center.distance(circle2.center)
        return radius + circle2.radius >= distance
    }

    /**
     * intersezione con il point
     *
     * @param point
     * @return
     */
    fun intersect(point: Point2?): Boolean {
        val distance2 = center.distance2(point)
        return distance2 < radius * radius
    }

    /**
     * Impostiamo il valore del cerchio
     *
     * @param centerValue
     * @param radiusValue
     */
    operator fun set(centerValue: Point2, radiusValue: Float) {
        center.x = centerValue.x
        center.y = centerValue.y
        radius = radiusValue
    }

    /**
     * Imposta il centro ignorando la z.
     * @param centerValue
     * @param radiusValue
     */
    operator fun set(centerValue: Point3, radiusValue: Float) {
        center.x = centerValue.x
        center.y = centerValue.y
        radius = radiusValue
    }

    fun copy(): Circle {
        return Circle(center.x, center.y, radius)
    }

    fun copyInto(destination: Circle) {
        center.copyInto(destination.center)
        destination.radius = radius
    }
}
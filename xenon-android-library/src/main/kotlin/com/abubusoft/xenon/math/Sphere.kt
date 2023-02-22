package com.abubusoft.xenon.math

class Sphere() {
    /**
     * Imposta le coordinate del centro della sfera.
     *
     * @param centerX
     * @param centerY
     * @param centerZ
     * @param radius
     */
    constructor(centerX: Float, centerY: Float, centerZ: Float, radius: Float) : this() {
        center.x = centerX
        center.y = centerY
        center.z = centerZ
        this.radius = radius
    }

    val center: Point3
    var radius = 0f

    init {
        center = Point3()
    }

    /**
     * Indica se i due cerchi si intersecano.
     *
     * Se la distanza tra i due centri è minore o uguale alla somma dei due
     * raggi, allora si intersecano necessariamente.
     *
     * @param sphere2
     * @return true se collidono, false altrimenti
     */
    fun intersect(sphere2: Sphere): Boolean {
        val distance = center.distance(sphere2.center)
        return radius + sphere2.radius >= distance
    }

    /**
     * @param point
     * @return
     * true se i due oggetti collidono
     */
    fun intersect(point: Point3?): Boolean {
        val distance2 = center.distance2(point!!)
        return distance2 < radius * radius
    }

    /**
     * Imposta il centro ed il raggio
     *
     * @param centerValue
     * @param radiusValue
     */
    operator fun set(centerValue: Point3, radiusValue: Float) {
        center.x = centerValue.x
        center.y = centerValue.y
        center.z = centerValue.z
        radius = radiusValue
    }

    fun copy(): Sphere {
        return Sphere(center.x, center.y, center.z, radius)
    }

    fun copyInto(destination: Sphere) {
        destination.center.x = center.x
        destination.center.y = center.y
        destination.center.z = center.z
        destination.radius = radius
    }
}